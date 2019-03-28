

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;


CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;



COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';



CREATE TYPE public.box2d;




CREATE TYPE public.box2df;




CREATE TYPE public.box3d;




CREATE TYPE public.geography;




CREATE TYPE public.geometry;




CREATE TYPE public.gidx;




CREATE TYPE public.histogram AS (
	min double precision,
	max double precision,
	count bigint,
	percent double precision
);




COMMENT ON TYPE public.histogram IS 'postgis raster type: A composite type used as record output of the ST_Histogram and ST_ApproxHistogram functions.';



CREATE TYPE public.pgis_abs;




CREATE TYPE public.quantile AS (
	quantile double precision,
	value double precision
);




CREATE TYPE public.raster;




COMMENT ON TYPE public.raster IS 'postgis raster type: raster spatial data type.';



CREATE TYPE public.reclassarg AS (
	nband integer,
	reclassexpr text,
	pixeltype text,
	nodataval double precision
);




COMMENT ON TYPE public.reclassarg IS 'postgis raster type: A composite type used as input into the ST_Reclass function defining the behavior of reclassification.';



CREATE TYPE public.spheroid;




CREATE TYPE public.summarystats AS (
	count bigint,
	sum double precision,
	mean double precision,
	stddev double precision,
	min double precision,
	max double precision
);




COMMENT ON TYPE public.summarystats IS 'postgis raster type: A composite type used as output of the ST_SummaryStats function.';



CREATE TYPE public.valuecount AS (
	value double precision,
	count integer,
	percent double precision
);




CREATE FUNCTION public._add_overview_constraint(ovschema name, ovtable name, ovcolumn name, refschema name, reftable name, refcolumn name, factor integer) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_overview_' || $3;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (_overview_constraint(' || quote_ident($3)
			|| ',' || $7
			|| ',' || quote_literal($4)
			|| ',' || quote_literal($5)
			|| ',' || quote_literal($6)
			|| '))';

		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint(cn name, sql text) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $$
	BEGIN
		BEGIN
			EXECUTE sql;
		EXCEPTION
			WHEN duplicate_object THEN
				RAISE NOTICE 'The constraint "%" already exists.  To replace the existing constraint, delete the constraint and call ApplyRasterConstraints again', cn;
			WHEN OTHERS THEN
				RAISE NOTICE 'Unable to add constraint "%"', cn;
				RETURN FALSE;
		END;

		RETURN TRUE;
	END;
	$$;




CREATE FUNCTION public._add_raster_constraint_alignment(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr text;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_same_alignment_' || $3;

		sql := 'SELECT st_makeemptyraster(1, 1, upperleftx, upperlefty, scalex, scaley, skewx, skewy, srid) FROM st_metadata((SELECT '
			|| quote_ident($3)
			|| ' FROM ' || fqtn || ' LIMIT 1))';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the alignment of a sample raster';
			RETURN FALSE;
		END;

		sql := 'ALTER TABLE ' || fqtn ||
			' ADD CONSTRAINT ' || quote_ident(cn) ||
			' CHECK (st_samealignment(' || quote_ident($3) || ', ''' || attr || '''::raster))';
		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_blocksize(rastschema name, rasttable name, rastcolumn name, axis text) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr int;
	BEGIN
		IF lower($4) != 'width' AND lower($4) != 'height' THEN
			RAISE EXCEPTION 'axis must be either "width" or "height"';
			RETURN FALSE;
		END IF;

		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_' || $4 || '_' || $3;

		sql := 'SELECT st_' || $4 || '('
			|| quote_ident($3)
			|| ') FROM ' || fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the % of a sample raster', $4;
			RETURN FALSE;
		END;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (st_' || $4 || '('
			|| quote_ident($3)
			|| ') = ' || attr || ')';
		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_extent(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr text;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_max_extent_' || $3;

		sql := 'SELECT st_ashexewkb(st_convexhull(st_collect(st_convexhull('
			|| quote_ident($3)
			|| ')))) FROM '
			|| fqtn;
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the extent of a sample raster';
			RETURN FALSE;
		END;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (st_coveredby(st_convexhull('
			|| quote_ident($3)
			|| '), ''' || attr || '''::geometry))';
		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_nodata_values(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr double precision[];
		max int;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_nodata_values_' || $3;

		sql := 'SELECT _raster_constraint_nodata_values(' || quote_ident($3)
			|| ') FROM ' || fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the nodata values of a sample raster';
			RETURN FALSE;
		END;
		max := array_length(attr, 1);
		IF max < 1 OR max IS NULL THEN
			RAISE NOTICE 'Unable to get the nodata values of a sample raster';
			RETURN FALSE;
		END IF;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (_raster_constraint_nodata_values(' || quote_ident($3)
			|| ')::numeric(16,10)[] = ''{';
		FOR x in 1..max LOOP
			IF attr[x] IS NULL THEN
				sql := sql || 'NULL';
			ELSE
				sql := sql || attr[x];
			END IF;
			IF x < max THEN
				sql := sql || ',';
			END IF;
		END LOOP;
		sql := sql || '}''::numeric(16,10)[])';

		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_num_bands(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr int;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_num_bands_' || $3;

		sql := 'SELECT st_numbands(' || quote_ident($3)
			|| ') FROM ' || fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the number of bands of a sample raster';
			RETURN FALSE;
		END;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (st_numbands(' || quote_ident($3)
			|| ') = ' || attr
			|| ')';
		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_out_db(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr boolean[];
		max int;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_out_db_' || $3;

		sql := 'SELECT _raster_constraint_out_db(' || quote_ident($3)
			|| ') FROM ' || fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the out-of-database bands of a sample raster';
			RETURN FALSE;
		END;
		max := array_length(attr, 1);
		IF max < 1 OR max IS NULL THEN
			RAISE NOTICE 'Unable to get the out-of-database bands of a sample raster';
			RETURN FALSE;
		END IF;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (_raster_constraint_out_db(' || quote_ident($3)
			|| ') = ''{';
		FOR x in 1..max LOOP
			IF attr[x] IS FALSE THEN
				sql := sql || 'FALSE';
			ELSE
				sql := sql || 'TRUE';
			END IF;
			IF x < max THEN
				sql := sql || ',';
			END IF;
		END LOOP;
		sql := sql || '}''::boolean[])';

		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_pixel_types(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr text[];
		max int;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_pixel_types_' || $3;

		sql := 'SELECT _raster_constraint_pixel_types(' || quote_ident($3)
			|| ') FROM ' || fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the pixel types of a sample raster';
			RETURN FALSE;
		END;
		max := array_length(attr, 1);
		IF max < 1 OR max IS NULL THEN
			RAISE NOTICE 'Unable to get the pixel types of a sample raster';
			RETURN FALSE;
		END IF;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (_raster_constraint_pixel_types(' || quote_ident($3)
			|| ') = ''{';
		FOR x in 1..max LOOP
			sql := sql || '"' || attr[x] || '"';
			IF x < max THEN
				sql := sql || ',';
			END IF;
		END LOOP;
		sql := sql || '}''::text[])';

		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_regular_blocking(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
	BEGIN

		RAISE INFO 'The regular_blocking constraint is just a flag indicating that the column "%" is regularly blocked.  It is up to the end-user to ensure that the column is truely regularly blocked.', quote_ident($3);

		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_regular_blocking_' || $3;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (TRUE)';
		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_scale(rastschema name, rasttable name, rastcolumn name, axis character) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr double precision;
	BEGIN
		IF lower($4) != 'x' AND lower($4) != 'y' THEN
			RAISE EXCEPTION 'axis must be either "x" or "y"';
			RETURN FALSE;
		END IF;

		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_scale' || $4 || '_' || $3;

		sql := 'SELECT st_scale' || $4 || '('
			|| quote_ident($3)
			|| ') FROM '
			|| fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the %-scale of a sample raster', upper($4);
			RETURN FALSE;
		END;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (st_scale' || $4 || '('
			|| quote_ident($3)
			|| ')::numeric(16,10) = (' || attr || ')::numeric(16,10))';
		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._add_raster_constraint_srid(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
		cn name;
		sql text;
		attr int;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		cn := 'enforce_srid_' || $3;

		sql := 'SELECT st_srid('
			|| quote_ident($3)
			|| ') FROM ' || fqtn
			|| ' LIMIT 1';
		BEGIN
			EXECUTE sql INTO attr;
		EXCEPTION WHEN OTHERS THEN
			RAISE NOTICE 'Unable to get the SRID of a sample raster';
			RETURN FALSE;
		END;

		sql := 'ALTER TABLE ' || fqtn
			|| ' ADD CONSTRAINT ' || quote_ident(cn)
			|| ' CHECK (st_srid('
			|| quote_ident($3)
			|| ') = ' || attr || ')';

		RETURN _add_raster_constraint(cn, sql);
	END;
	$_$;




CREATE FUNCTION public._drop_overview_constraint(ovschema name, ovtable name, ovcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_overview_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint(rastschema name, rasttable name, cn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		fqtn text;
	BEGIN
		fqtn := '';
		IF length($1) > 0 THEN
			fqtn := quote_ident($1) || '.';
		END IF;
		fqtn := fqtn || quote_ident($2);

		BEGIN
			EXECUTE 'ALTER TABLE '
				|| fqtn
				|| ' DROP CONSTRAINT '
				|| quote_ident(cn);
			RETURN TRUE;
		EXCEPTION
			WHEN undefined_object THEN
				RAISE NOTICE 'The constraint "%" does not exist.  Skipping', cn;
			WHEN OTHERS THEN
				RAISE NOTICE 'Unable to drop constraint "%"', cn;
				RETURN FALSE;
		END;

		RETURN TRUE;
	END;
	$_$;




CREATE FUNCTION public._drop_raster_constraint_alignment(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_same_alignment_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_blocksize(rastschema name, rasttable name, rastcolumn name, axis text) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	BEGIN
		IF lower($4) != 'width' AND lower($4) != 'height' THEN
			RAISE EXCEPTION 'axis must be either "width" or "height"';
			RETURN FALSE;
		END IF;

		RETURN _drop_raster_constraint($1, $2, 'enforce_' || $4 || '_' || $3);
	END;
	$_$;




CREATE FUNCTION public._drop_raster_constraint_extent(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_max_extent_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_nodata_values(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_nodata_values_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_num_bands(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_num_bands_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_out_db(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_out_db_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_pixel_types(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_pixel_types_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_regular_blocking(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_regular_blocking_' || $3) $_$;




CREATE FUNCTION public._drop_raster_constraint_scale(rastschema name, rasttable name, rastcolumn name, axis character) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	BEGIN
		IF lower($4) != 'x' AND lower($4) != 'y' THEN
			RAISE EXCEPTION 'axis must be either "x" or "y"';
			RETURN FALSE;
		END IF;

		RETURN _drop_raster_constraint($1, $2, 'enforce_scale' || $4 || '_' || $3);
	END;
	$_$;




CREATE FUNCTION public._drop_raster_constraint_srid(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT _drop_raster_constraint($1, $2, 'enforce_srid_' || $3) $_$;




CREATE FUNCTION public._overview_constraint_info(ovschema name, ovtable name, ovcolumn name, OUT refschema name, OUT reftable name, OUT refcolumn name, OUT factor integer) RETURNS record
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		split_part(split_part(s.consrc, '''::name', 1), '''', 2)::name,
		split_part(split_part(s.consrc, '''::name', 2), '''', 2)::name,
		split_part(split_part(s.consrc, '''::name', 3), '''', 2)::name,
		trim(both from split_part(s.consrc, ',', 2))::integer
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%_overview_constraint(%'
	$_$;




CREATE FUNCTION public._raster_constraint_info_alignment(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		TRUE
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%st_samealignment(%';
	$_$;




CREATE FUNCTION public._raster_constraint_info_blocksize(rastschema name, rasttable name, rastcolumn name, axis text) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', '')::integer
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%st_' || $4 || '(% = %';
	$_$;




CREATE FUNCTION public._raster_constraint_info_nodata_values(rastschema name, rasttable name, rastcolumn name) RETURNS double precision[]
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		trim(both '''' from split_part(replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', ''), '::', 1))::double precision[]
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%_raster_constraint_nodata_values(%';
	$_$;




CREATE FUNCTION public._raster_constraint_info_num_bands(rastschema name, rasttable name, rastcolumn name) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', '')::integer
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%st_numbands(%';
	$_$;




CREATE FUNCTION public._raster_constraint_info_out_db(rastschema name, rasttable name, rastcolumn name) RETURNS boolean[]
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		trim(both '''' from split_part(replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', ''), '::', 1))::boolean[]
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%_raster_constraint_out_db(%';
	$_$;




CREATE FUNCTION public._raster_constraint_info_pixel_types(rastschema name, rasttable name, rastcolumn name) RETURNS text[]
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		trim(both '''' from split_part(replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', ''), '::', 1))::text[]
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%_raster_constraint_pixel_types(%';
	$_$;




CREATE FUNCTION public._raster_constraint_info_regular_blocking(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
    LANGUAGE plpgsql STABLE STRICT
    AS $_$
	DECLARE
		cn text;
		sql text;
		rtn boolean;
	BEGIN
		cn := 'enforce_regular_blocking_' || $3;

		sql := 'SELECT TRUE FROM pg_class c, pg_namespace n, pg_constraint s'
			|| ' WHERE n.nspname = ' || quote_literal($1)
			|| ' AND c.relname = ' || quote_literal($2)
			|| ' AND s.connamespace = n.oid AND s.conrelid = c.oid'
			|| ' AND s.conname = ' || quote_literal(cn);
		EXECUTE sql INTO rtn;
		RETURN rtn;
	END;
	$_$;




CREATE FUNCTION public._raster_constraint_info_scale(rastschema name, rasttable name, rastcolumn name, axis character) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		replace(replace(split_part(split_part(s.consrc, ' = ', 2), '::', 1), ')', ''), '(', '')::double precision
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%st_scale' || $4 || '(% = %';
	$_$;




CREATE FUNCTION public._raster_constraint_info_srid(rastschema name, rasttable name, rastcolumn name) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', '')::integer
	FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
	WHERE n.nspname = $1
		AND c.relname = $2
		AND a.attname = $3
		AND a.attrelid = c.oid
		AND s.connamespace = n.oid
		AND s.conrelid = c.oid
		AND a.attnum = ANY (s.conkey)
		AND s.consrc LIKE '%st_srid(% = %';
	$_$;




CREATE FUNCTION public._st_aspect4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        pwidth float;
        pheight float;
        dz_dx float;
        dz_dy float;
        aspect float;
    BEGIN
        pwidth := args[1]::float;
        pheight := args[2]::float;
        dz_dx := ((matrix[3][1] + 2.0 * matrix[3][2] + matrix[3][3]) - (matrix[1][1] + 2.0 * matrix[1][2] + matrix[1][3])) / (8.0 * pwidth);
        dz_dy := ((matrix[1][3] + 2.0 * matrix[2][3] + matrix[3][3]) - (matrix[1][1] + 2.0 * matrix[2][1] + matrix[3][1])) / (8.0 * pheight);
        IF abs(dz_dx) = 0::float AND abs(dz_dy) = 0::float THEN
            RETURN -1;
        END IF;

        aspect := atan2(dz_dy, -dz_dx);
        IF aspect > (pi() / 2.0) THEN
            RETURN (5.0 * pi() / 2.0) - aspect;
        ELSE
            RETURN (pi() / 2.0) - aspect;
        END IF;
    END;
    $$;




CREATE FUNCTION public._st_count(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 1) RETURNS bigint
    LANGUAGE plpgsql STABLE STRICT
    AS $_$
	DECLARE
		curs refcursor;

		ctable text;
		ccolumn text;
		rast raster;
		stats summarystats;

		rtn bigint;
		tmp bigint;
	BEGIN
		IF nband < 1 THEN
			RAISE WARNING 'Invalid band index (must use 1-based). Returning NULL';
			RETURN NULL;
		END IF;

		IF sample_percent < 0 OR sample_percent > 1 THEN
			RAISE WARNING 'Invalid sample percentage (must be between 0 and 1). Returning NULL';
			RETURN NULL;
		END IF;

		IF exclude_nodata_value IS TRUE THEN
			SELECT count INTO rtn FROM _st_summarystats($1, $2, $3, $4, $5);
			RETURN rtn;
		END IF;

		ctable := quote_ident(rastertable);
		ccolumn := quote_ident(rastercolumn);

		BEGIN
			OPEN curs FOR EXECUTE 'SELECT '
					|| ccolumn
					|| ' FROM '
					|| ctable
					|| ' WHERE '
					|| ccolumn
					|| ' IS NOT NULL';
		EXCEPTION
			WHEN OTHERS THEN
				RAISE WARNING 'Invalid table or column name. Returning NULL';
				RETURN NULL;
		END;

		rtn := 0;
		LOOP
			FETCH curs INTO rast;
			EXIT WHEN NOT FOUND;

			SELECT (width * height) INTO tmp FROM ST_Metadata(rast);
			rtn := rtn + tmp;
		END LOOP;

		CLOSE curs;

		RETURN rtn;
	END;
	$_$;




CREATE FUNCTION public._st_hillshade4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        pwidth float;
        pheight float;
        dz_dx float;
        dz_dy float;
        zenith float;
        azimuth float;
        slope float;
        aspect float;
        max_bright float;
        elevation_scale float;
    BEGIN
        pwidth := args[1]::float;
        pheight := args[2]::float;
        azimuth := (5.0 * pi() / 2.0) - args[3]::float;
        zenith := (pi() / 2.0) - args[4]::float;
        dz_dx := ((matrix[3][1] + 2.0 * matrix[3][2] + matrix[3][3]) - (matrix[1][1] + 2.0 * matrix[1][2] + matrix[1][3])) / (8.0 * pwidth);
        dz_dy := ((matrix[1][3] + 2.0 * matrix[2][3] + matrix[3][3]) - (matrix[1][1] + 2.0 * matrix[2][1] + matrix[3][1])) / (8.0 * pheight);
        elevation_scale := args[6]::float;
        slope := atan(sqrt(elevation_scale * pow(dz_dx, 2.0) + pow(dz_dy, 2.0)));
        IF abs(dz_dy) = 0::float AND abs(dz_dy) = 0::float THEN
            aspect := pi();
        ELSE
            aspect := atan2(dz_dy, -dz_dx);
        END IF;
        max_bright := args[5]::float;

        IF aspect < 0 THEN
            aspect := aspect + (2.0 * pi());
        END IF;

        RETURN max_bright * ( (cos(zenith)*cos(slope)) + (sin(zenith)*sin(slope)*cos(azimuth - aspect)) );
    END;
    $$;




CREATE FUNCTION public._st_slope4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        pwidth float;
        pheight float;
        dz_dx float;
        dz_dy float;
    BEGIN
        pwidth := args[1]::float;
        pheight := args[2]::float;
        dz_dx := ((matrix[3][1] + 2.0 * matrix[3][2] + matrix[3][3]) - (matrix[1][1] + 2.0 * matrix[1][2] + matrix[1][3])) / (8.0 * pwidth);
        dz_dy := ((matrix[1][3] + 2.0 * matrix[2][3] + matrix[3][3]) - (matrix[1][1] + 2.0 * matrix[2][1] + matrix[3][1])) / (8.0 * pheight);
        RETURN atan(sqrt(pow(dz_dx, 2.0) + pow(dz_dy, 2.0)));
    END;
    $$;




CREATE FUNCTION public.addauth(text) RETURNS boolean
    LANGUAGE plpgsql
    AS $_$ 
DECLARE
	lockid alias for $1;
	okay boolean;
	myrec record;
BEGIN
	okay := 'f';
	FOR myrec IN SELECT * FROM pg_class WHERE relname = 'temp_lock_have_table' LOOP
		okay := 't';
	END LOOP; 
	IF (okay <> 't') THEN 
		CREATE TEMP TABLE temp_lock_have_table (transid xid, lockcode text);
	END IF;


	INSERT INTO temp_lock_have_table VALUES (getTransactionID(), lockid);

	RETURN true::boolean;
END;
$_$;




CREATE FUNCTION public.addgeometrycolumn(table_name character varying, column_name character varying, new_srid integer, new_type character varying, new_dim integer, use_typmod boolean DEFAULT true) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT AddGeometryColumn('','',$1,$2,$3,$4,$5, $6) into ret;
	RETURN ret;
END;
$_$;




CREATE FUNCTION public.addgeometrycolumn(schema_name character varying, table_name character varying, column_name character varying, new_srid integer, new_type character varying, new_dim integer, use_typmod boolean DEFAULT true) RETURNS text
    LANGUAGE plpgsql STABLE STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT AddGeometryColumn('',$1,$2,$3,$4,$5,$6,$7) into ret;
	RETURN ret;
END;
$_$;




CREATE FUNCTION public.addgeometrycolumn(catalog_name character varying, schema_name character varying, table_name character varying, column_name character varying, new_srid_in integer, new_type character varying, new_dim integer, use_typmod boolean DEFAULT true) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $$
DECLARE
	rec RECORD;
	sr varchar;
	real_schema name;
	sql text;
	new_srid integer;

BEGIN

	IF (postgis_type_name(new_type,new_dim) IS NULL )
	THEN
		RAISE EXCEPTION 'Invalid type name "%(%)" - valid ones are:
	POINT, MULTIPOINT,
	LINESTRING, MULTILINESTRING,
	POLYGON, MULTIPOLYGON,
	CIRCULARSTRING, COMPOUNDCURVE, MULTICURVE,
	CURVEPOLYGON, MULTISURFACE,
	GEOMETRY, GEOMETRYCOLLECTION,
	POINTM, MULTIPOINTM,
	LINESTRINGM, MULTILINESTRINGM,
	POLYGONM, MULTIPOLYGONM,
	CIRCULARSTRINGM, COMPOUNDCURVEM, MULTICURVEM
	CURVEPOLYGONM, MULTISURFACEM, TRIANGLE, TRIANGLEM,
	POLYHEDRALSURFACE, POLYHEDRALSURFACEM, TIN, TINM
	or GEOMETRYCOLLECTIONM', new_type, new_dim;
		RETURN 'fail';
	END IF;


	IF ( (new_dim >4) OR (new_dim <2) ) THEN
		RAISE EXCEPTION 'invalid dimension';
		RETURN 'fail';
	END IF;

	IF ( (new_type LIKE '%M') AND (new_dim!=3) ) THEN
		RAISE EXCEPTION 'TypeM needs 3 dimensions';
		RETURN 'fail';
	END IF;


	IF ( new_srid_in > 0 ) THEN
		IF new_srid_in > 998999 THEN
			RAISE EXCEPTION 'AddGeometryColumn() - SRID must be <= %', 998999;
		END IF;
		new_srid := new_srid_in;
		SELECT SRID INTO sr FROM spatial_ref_sys WHERE SRID = new_srid;
		IF NOT FOUND THEN
			RAISE EXCEPTION 'AddGeometryColumn() - invalid SRID';
			RETURN 'fail';
		END IF;
	ELSE
		new_srid := ST_SRID('POINT EMPTY'::geometry);
		IF ( new_srid_in != new_srid ) THEN
			RAISE NOTICE 'SRID value % converted to the officially unknown SRID value %', new_srid_in, new_srid;
		END IF;
	END IF;


	IF ( schema_name IS NOT NULL AND schema_name != '' ) THEN
		sql := 'SELECT nspname FROM pg_namespace ' ||
			'WHERE text(nspname) = ' || quote_literal(schema_name) ||
			'LIMIT 1';
		RAISE DEBUG '%', sql;
		EXECUTE sql INTO real_schema;

		IF ( real_schema IS NULL ) THEN
			RAISE EXCEPTION 'Schema % is not a valid schemaname', quote_literal(schema_name);
			RETURN 'fail';
		END IF;
	END IF;

	IF ( real_schema IS NULL ) THEN
		RAISE DEBUG 'Detecting schema';
		sql := 'SELECT n.nspname AS schemaname ' ||
			'FROM pg_catalog.pg_class c ' ||
			  'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace ' ||
			'WHERE c.relkind = ' || quote_literal('r') ||
			' AND n.nspname NOT IN (' || quote_literal('pg_catalog') || ', ' || quote_literal('pg_toast') || ')' ||
			' AND pg_catalog.pg_table_is_visible(c.oid)' ||
			' AND c.relname = ' || quote_literal(table_name);
		RAISE DEBUG '%', sql;
		EXECUTE sql INTO real_schema;

		IF ( real_schema IS NULL ) THEN
			RAISE EXCEPTION 'Table % does not occur in the search_path', quote_literal(table_name);
			RETURN 'fail';
		END IF;
	END IF;


	IF use_typmod THEN
	     sql := 'ALTER TABLE ' ||
            quote_ident(real_schema) || '.' || quote_ident(table_name)
            || ' ADD COLUMN ' || quote_ident(column_name) ||
            ' geometry(' || postgis_type_name(new_type, new_dim) || ', ' || new_srid::text || ')';
        RAISE DEBUG '%', sql;
	ELSE
        sql := 'ALTER TABLE ' ||
            quote_ident(real_schema) || '.' || quote_ident(table_name)
            || ' ADD COLUMN ' || quote_ident(column_name) ||
            ' geometry ';
        RAISE DEBUG '%', sql;
    END IF;
	EXECUTE sql;

	IF NOT use_typmod THEN
        sql := 'ALTER TABLE ' ||
            quote_ident(real_schema) || '.' || quote_ident(table_name)
            || ' ADD CONSTRAINT '
            || quote_ident('enforce_srid_' || column_name)
            || ' CHECK (st_srid(' || quote_ident(column_name) ||
            ') = ' || new_srid::text || ')' ;
        RAISE DEBUG '%', sql;
        EXECUTE sql;
    
        sql := 'ALTER TABLE ' ||
            quote_ident(real_schema) || '.' || quote_ident(table_name)
            || ' ADD CONSTRAINT '
            || quote_ident('enforce_dims_' || column_name)
            || ' CHECK (st_ndims(' || quote_ident(column_name) ||
            ') = ' || new_dim::text || ')' ;
        RAISE DEBUG '%', sql;
        EXECUTE sql;
    
        IF ( NOT (new_type = 'GEOMETRY')) THEN
            sql := 'ALTER TABLE ' ||
                quote_ident(real_schema) || '.' || quote_ident(table_name) || ' ADD CONSTRAINT ' ||
                quote_ident('enforce_geotype_' || column_name) ||
                ' CHECK (GeometryType(' ||
                quote_ident(column_name) || ')=' ||
                quote_literal(new_type) || ' OR (' ||
                quote_ident(column_name) || ') is null)';
            RAISE DEBUG '%', sql;
            EXECUTE sql;
        END IF;
    END IF;

	RETURN
		real_schema || '.' ||
		table_name || '.' || column_name ||
		' SRID:' || new_srid::text ||
		' TYPE:' || new_type ||
		' DIMS:' || new_dim::text || ' ';
END;
$$;




CREATE FUNCTION public.addoverviewconstraints(ovtable name, ovcolumn name, reftable name, refcolumn name, ovfactor integer) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT AddOverviewConstraints('', $1, $2, '', $3, $4, $5) $_$;




CREATE FUNCTION public.addoverviewconstraints(ovschema name, ovtable name, ovcolumn name, refschema name, reftable name, refcolumn name, ovfactor integer) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		x int;
		s name;
		t name;
		oschema name;
		rschema name;
		sql text;
		rtn boolean;
	BEGIN
		FOR x IN 1..2 LOOP
			s := '';

			IF x = 1 THEN
				s := $1;
				t := $2;
			ELSE
				s := $4;
				t := $5;
			END IF;

			IF length(s) > 0 THEN
				sql := 'SELECT nspname FROM pg_namespace '
					|| 'WHERE nspname = ' || quote_literal(s)
					|| 'LIMIT 1';
				EXECUTE sql INTO s;

				IF s IS NULL THEN
					RAISE EXCEPTION 'The value % is not a valid schema', quote_literal(s);
					RETURN FALSE;
				END IF;
			END IF;

			IF length(s) < 1 THEN
				sql := 'SELECT n.nspname AS schemaname '
					|| 'FROM pg_catalog.pg_class c '
					|| 'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace '
					|| 'WHERE c.relkind = ' || quote_literal('r')
					|| ' AND n.nspname NOT IN (' || quote_literal('pg_catalog')
					|| ', ' || quote_literal('pg_toast')
					|| ') AND pg_catalog.pg_table_is_visible(c.oid)'
					|| ' AND c.relname = ' || quote_literal(t);
				EXECUTE sql INTO s;

				IF s IS NULL THEN
					RAISE EXCEPTION 'The table % does not occur in the search_path', quote_literal(t);
					RETURN FALSE;
				END IF;
			END IF;

			IF x = 1 THEN
				oschema := s;
			ELSE
				rschema := s;
			END IF;
		END LOOP;

		rtn := _add_overview_constraint(oschema, $2, $3, rschema, $5, $6, $7);
		IF rtn IS FALSE THEN
			RAISE EXCEPTION 'Unable to add the overview constraint.  Is the schema name, table name or column name incorrect?';
			RETURN FALSE;
		END IF;

		RETURN TRUE;
	END;
	$_$;




CREATE FUNCTION public.addrasterconstraints(rasttable name, rastcolumn name, VARIADIC constraints text[]) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT AddRasterConstraints('', $1, $2, VARIADIC $3) $_$;




COMMENT ON FUNCTION public.addrasterconstraints(rasttable name, rastcolumn name, VARIADIC constraints text[]) IS 'args: rasttable, rastcolumn, VARIADIC constraints - Adds raster constraints to a loaded raster table for a specific column that constrains spatial ref, scaling, blocksize, alignment, bands, band type and a flag to denote if raster column is regularly blocked. The table must be loaded with data for the constraints to be inferred. Returns true of the constraint setting was accomplished and if issues a notice.';



CREATE FUNCTION public.addrasterconstraints(rastschema name, rasttable name, rastcolumn name, VARIADIC constraints text[]) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		max int;
		cnt int;
		sql text;
		schema name;
		x int;
		kw text;
		rtn boolean;
	BEGIN
		cnt := 0;
		max := array_length(constraints, 1);
		IF max < 1 THEN
			RAISE NOTICE 'No constraints indicated to be added.  Doing nothing';
			RETURN TRUE;
		END IF;

		schema := NULL;
		IF length($1) > 0 THEN
			sql := 'SELECT nspname FROM pg_namespace '
				|| 'WHERE nspname = ' || quote_literal($1)
				|| 'LIMIT 1';
			EXECUTE sql INTO schema;

			IF schema IS NULL THEN
				RAISE EXCEPTION 'The value provided for schema is invalid';
				RETURN FALSE;
			END IF;
		END IF;

		IF schema IS NULL THEN
			sql := 'SELECT n.nspname AS schemaname '
				|| 'FROM pg_catalog.pg_class c '
				|| 'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace '
				|| 'WHERE c.relkind = ' || quote_literal('r')
				|| ' AND n.nspname NOT IN (' || quote_literal('pg_catalog')
				|| ', ' || quote_literal('pg_toast')
				|| ') AND pg_catalog.pg_table_is_visible(c.oid)'
				|| ' AND c.relname = ' || quote_literal($2);
			EXECUTE sql INTO schema;

			IF schema IS NULL THEN
				RAISE EXCEPTION 'The table % does not occur in the search_path', quote_literal($2);
				RETURN FALSE;
			END IF;
		END IF;

		<<kwloop>>
		FOR x in 1..max LOOP
			kw := trim(both from lower(constraints[x]));

			BEGIN
				CASE
					WHEN kw = 'srid' THEN
						RAISE NOTICE 'Adding SRID constraint';
						rtn := _add_raster_constraint_srid(schema, $2, $3);
					WHEN kw IN ('scale_x', 'scalex') THEN
						RAISE NOTICE 'Adding scale-X constraint';
						rtn := _add_raster_constraint_scale(schema, $2, $3, 'x');
					WHEN kw IN ('scale_y', 'scaley') THEN
						RAISE NOTICE 'Adding scale-Y constraint';
						rtn := _add_raster_constraint_scale(schema, $2, $3, 'y');
					WHEN kw = 'scale' THEN
						RAISE NOTICE 'Adding scale-X constraint';
						rtn := _add_raster_constraint_scale(schema, $2, $3, 'x');
						RAISE NOTICE 'Adding scale-Y constraint';
						rtn := _add_raster_constraint_scale(schema, $2, $3, 'y');
					WHEN kw IN ('blocksize_x', 'blocksizex', 'width') THEN
						RAISE NOTICE 'Adding blocksize-X constraint';
						rtn := _add_raster_constraint_blocksize(schema, $2, $3, 'width');
					WHEN kw IN ('blocksize_y', 'blocksizey', 'height') THEN
						RAISE NOTICE 'Adding blocksize-Y constraint';
						rtn := _add_raster_constraint_blocksize(schema, $2, $3, 'height');
					WHEN kw = 'blocksize' THEN
						RAISE NOTICE 'Adding blocksize-X constraint';
						rtn := _add_raster_constraint_blocksize(schema, $2, $3, 'width');
						RAISE NOTICE 'Adding blocksize-Y constraint';
						rtn := _add_raster_constraint_blocksize(schema, $2, $3, 'height');
					WHEN kw IN ('same_alignment', 'samealignment', 'alignment') THEN
						RAISE NOTICE 'Adding alignment constraint';
						rtn := _add_raster_constraint_alignment(schema, $2, $3);
					WHEN kw IN ('regular_blocking', 'regularblocking') THEN
						RAISE NOTICE 'Adding regular blocking constraint';
						rtn := _add_raster_constraint_regular_blocking(schema, $2, $3);
					WHEN kw IN ('num_bands', 'numbands') THEN
						RAISE NOTICE 'Adding number of bands constraint';
						rtn := _add_raster_constraint_num_bands(schema, $2, $3);
					WHEN kw IN ('pixel_types', 'pixeltypes') THEN
						RAISE NOTICE 'Adding pixel type constraint';
						rtn := _add_raster_constraint_pixel_types(schema, $2, $3);
					WHEN kw IN ('nodata_values', 'nodatavalues', 'nodata') THEN
						RAISE NOTICE 'Adding nodata value constraint';
						rtn := _add_raster_constraint_nodata_values(schema, $2, $3);
					WHEN kw IN ('out_db', 'outdb') THEN
						RAISE NOTICE 'Adding out-of-database constraint';
						rtn := _add_raster_constraint_out_db(schema, $2, $3);
					WHEN kw = 'extent' THEN
						RAISE NOTICE 'Adding maximum extent constraint';
						rtn := _add_raster_constraint_extent(schema, $2, $3);
					ELSE
						RAISE NOTICE 'Unknown constraint: %.  Skipping', quote_literal(constraints[x]);
						CONTINUE kwloop;
				END CASE;
			END;

			IF rtn IS FALSE THEN
				cnt := cnt + 1;
				RAISE WARNING 'Unable to add constraint: %.  Skipping', quote_literal(constraints[x]);
			END IF;

		END LOOP kwloop;

		IF cnt = max THEN
			RAISE EXCEPTION 'None of the constraints specified could be added.  Is the schema name, table name or column name incorrect?';
			RETURN FALSE;
		END IF;

		RETURN TRUE;
	END;
	$_$;




COMMENT ON FUNCTION public.addrasterconstraints(rastschema name, rasttable name, rastcolumn name, VARIADIC constraints text[]) IS 'args: rastschema, rasttable, rastcolumn, VARIADIC constraints - Adds raster constraints to a loaded raster table for a specific column that constrains spatial ref, scaling, blocksize, alignment, bands, band type and a flag to denote if raster column is regularly blocked. The table must be loaded with data for the constraints to be inferred. Returns true of the constraint setting was accomplished and if issues a notice.';



CREATE FUNCTION public.addrasterconstraints(rasttable name, rastcolumn name, srid boolean DEFAULT true, scale_x boolean DEFAULT true, scale_y boolean DEFAULT true, blocksize_x boolean DEFAULT true, blocksize_y boolean DEFAULT true, same_alignment boolean DEFAULT true, regular_blocking boolean DEFAULT false, num_bands boolean DEFAULT true, pixel_types boolean DEFAULT true, nodata_values boolean DEFAULT true, out_db boolean DEFAULT true, extent boolean DEFAULT true) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT AddRasterConstraints('', $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14) $_$;




COMMENT ON FUNCTION public.addrasterconstraints(rasttable name, rastcolumn name, srid boolean, scale_x boolean, scale_y boolean, blocksize_x boolean, blocksize_y boolean, same_alignment boolean, regular_blocking boolean, num_bands boolean, pixel_types boolean, nodata_values boolean, out_db boolean, extent boolean) IS 'args: rasttable, rastcolumn, srid, scale_x, scale_y, blocksize_x, blocksize_y, same_alignment, regular_blocking, num_bands=true, pixel_types=true, nodata_values=true, out_db=true, extent=true - Adds raster constraints to a loaded raster table for a specific column that constrains spatial ref, scaling, blocksize, alignment, bands, band type and a flag to denote if raster column is regularly blocked. The table must be loaded with data for the constraints to be inferred. Returns true of the constraint setting was accomplished and if issues a notice.';



CREATE FUNCTION public.addrasterconstraints(rastschema name, rasttable name, rastcolumn name, srid boolean DEFAULT true, scale_x boolean DEFAULT true, scale_y boolean DEFAULT true, blocksize_x boolean DEFAULT true, blocksize_y boolean DEFAULT true, same_alignment boolean DEFAULT true, regular_blocking boolean DEFAULT false, num_bands boolean DEFAULT true, pixel_types boolean DEFAULT true, nodata_values boolean DEFAULT true, out_db boolean DEFAULT true, extent boolean DEFAULT true) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		constraints text[];
	BEGIN
		IF srid IS TRUE THEN
			constraints := constraints || 'srid'::text;
		END IF;

		IF scale_x IS TRUE THEN
			constraints := constraints || 'scale_x'::text;
		END IF;

		IF scale_y IS TRUE THEN
			constraints := constraints || 'scale_y'::text;
		END IF;

		IF blocksize_x IS TRUE THEN
			constraints := constraints || 'blocksize_x'::text;
		END IF;

		IF blocksize_y IS TRUE THEN
			constraints := constraints || 'blocksize_y'::text;
		END IF;

		IF same_alignment IS TRUE THEN
			constraints := constraints || 'same_alignment'::text;
		END IF;

		IF regular_blocking IS TRUE THEN
			constraints := constraints || 'regular_blocking'::text;
		END IF;

		IF num_bands IS TRUE THEN
			constraints := constraints || 'num_bands'::text;
		END IF;

		IF pixel_types IS TRUE THEN
			constraints := constraints || 'pixel_types'::text;
		END IF;

		IF nodata_values IS TRUE THEN
			constraints := constraints || 'nodata_values'::text;
		END IF;

		IF out_db IS TRUE THEN
			constraints := constraints || 'out_db'::text;
		END IF;

		IF extent IS TRUE THEN
			constraints := constraints || 'extent'::text;
		END IF;

		RETURN AddRasterConstraints($1, $2, $3, VARIADIC constraints);
	END;
	$_$;




COMMENT ON FUNCTION public.addrasterconstraints(rastschema name, rasttable name, rastcolumn name, srid boolean, scale_x boolean, scale_y boolean, blocksize_x boolean, blocksize_y boolean, same_alignment boolean, regular_blocking boolean, num_bands boolean, pixel_types boolean, nodata_values boolean, out_db boolean, extent boolean) IS 'args: rastschema, rasttable, rastcolumn, srid=true, scale_x=true, scale_y=true, blocksize_x=true, blocksize_y=true, same_alignment=true, regular_blocking=true, num_bands=true, pixel_types=true, nodata_values=true, out_db=true, extent=true - Adds raster constraints to a loaded raster table for a specific column that constrains spatial ref, scaling, blocksize, alignment, bands, band type and a flag to denote if raster column is regularly blocked. The table must be loaded with data for the constraints to be inferred. Returns true of the constraint setting was accomplished and if issues a notice.';



CREATE FUNCTION public.checkauth(text, text) RETURNS integer
    LANGUAGE sql
    AS $_$ SELECT CheckAuth('', $1, $2) $_$;




CREATE FUNCTION public.checkauth(text, text, text) RETURNS integer
    LANGUAGE plpgsql
    AS $_$ 
DECLARE
	schema text;
BEGIN
	IF NOT LongTransactionsEnabled() THEN
		RAISE EXCEPTION 'Long transaction support disabled, use EnableLongTransaction() to enable.';
	END IF;

	if ( $1 != '' ) THEN
		schema = $1;
	ELSE
		SELECT current_schema() into schema;
	END IF;


	EXECUTE 'CREATE TRIGGER check_auth BEFORE UPDATE OR DELETE ON ' 
		|| quote_ident(schema) || '.' || quote_ident($2)
		||' FOR EACH ROW EXECUTE PROCEDURE CheckAuthTrigger('
		|| quote_literal($3) || ')';

	RETURN 0;
END;
$_$;




CREATE FUNCTION public.disablelongtransactions() RETURNS text
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	rec RECORD;

BEGIN

	FOR rec IN
		SELECT c.relname, t.tgname, t.tgargs FROM pg_trigger t, pg_class c, pg_proc p
		WHERE p.proname = 'checkauthtrigger' and t.tgfoid = p.oid and t.tgrelid = c.oid
	LOOP
		EXECUTE 'DROP TRIGGER ' || quote_ident(rec.tgname) ||
			' ON ' || quote_ident(rec.relname);
	END LOOP;

	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorization_table' LOOP
		DROP TABLE authorization_table;
	END LOOP;

	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorized_tables' LOOP
		DROP VIEW authorized_tables;
	END LOOP;

	RETURN 'Long transactions support disabled';
END;
$$;




CREATE FUNCTION public.dropgeometrycolumn(table_name character varying, column_name character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret text;
BEGIN
	SELECT DropGeometryColumn('','',$1,$2) into ret;
	RETURN ret;
END;
$_$;




CREATE FUNCTION public.dropgeometrycolumn(schema_name character varying, table_name character varying, column_name character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret text;
BEGIN
	SELECT DropGeometryColumn('',$1,$2,$3) into ret;
	RETURN ret;
END;
$_$;




CREATE FUNCTION public.dropgeometrycolumn(catalog_name character varying, schema_name character varying, table_name character varying, column_name character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $$
DECLARE
	myrec RECORD;
	okay boolean;
	real_schema name;

BEGIN


	IF ( schema_name != '' ) THEN
		okay = false;

		FOR myrec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			okay := true;
		END LOOP;

		IF ( okay <>  true ) THEN
			RAISE NOTICE 'Invalid schema name - using current_schema()';
			SELECT current_schema() into real_schema;
		ELSE
			real_schema = schema_name;
		END IF;
	ELSE
		SELECT current_schema() into real_schema;
	END IF;

	okay = false;
	FOR myrec IN SELECT * from geometry_columns where f_table_schema = text(real_schema) and f_table_name = table_name and f_geometry_column = column_name LOOP
		okay := true;
	END LOOP;
	IF (okay <> true) THEN
		RAISE EXCEPTION 'column not found in geometry_columns table';
		RETURN false;
	END IF;

	EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) || '.' ||
		quote_ident(table_name) || ' DROP COLUMN ' ||
		quote_ident(column_name);

	RETURN real_schema || '.' || table_name || '.' || column_name ||' effectively removed.';

END;
$$;




CREATE FUNCTION public.dropgeometrytable(table_name character varying) RETURNS text
    LANGUAGE sql STRICT
    AS $_$ SELECT DropGeometryTable('','',$1) $_$;




CREATE FUNCTION public.dropgeometrytable(schema_name character varying, table_name character varying) RETURNS text
    LANGUAGE sql STRICT
    AS $_$ SELECT DropGeometryTable('',$1,$2) $_$;




CREATE FUNCTION public.dropgeometrytable(catalog_name character varying, schema_name character varying, table_name character varying) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $$
DECLARE
	real_schema name;

BEGIN

	IF ( schema_name = '' ) THEN
		SELECT current_schema() into real_schema;
	ELSE
		real_schema = schema_name;
	END IF;

	EXECUTE 'DROP TABLE IF EXISTS '
		|| quote_ident(real_schema) || '.' ||
		quote_ident(table_name) || ' RESTRICT';

	RETURN
		real_schema || '.' ||
		table_name ||' dropped.';

END;
$$;




CREATE FUNCTION public.dropoverviewconstraints(ovtable name, ovcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT DropOverviewConstraints('', $1, $2) $_$;




CREATE FUNCTION public.dropoverviewconstraints(ovschema name, ovtable name, ovcolumn name) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		schema name;
		sql text;
		rtn boolean;
	BEGIN
		schema := NULL;
		IF length($1) > 0 THEN
			sql := 'SELECT nspname FROM pg_namespace '
				|| 'WHERE nspname = ' || quote_literal($1)
				|| 'LIMIT 1';
			EXECUTE sql INTO schema;

			IF schema IS NULL THEN
				RAISE EXCEPTION 'The value provided for schema is invalid';
				RETURN FALSE;
			END IF;
		END IF;

		IF schema IS NULL THEN
			sql := 'SELECT n.nspname AS schemaname '
				|| 'FROM pg_catalog.pg_class c '
				|| 'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace '
				|| 'WHERE c.relkind = ' || quote_literal('r')
				|| ' AND n.nspname NOT IN (' || quote_literal('pg_catalog')
				|| ', ' || quote_literal('pg_toast')
				|| ') AND pg_catalog.pg_table_is_visible(c.oid)'
				|| ' AND c.relname = ' || quote_literal($2);
			EXECUTE sql INTO schema;

			IF schema IS NULL THEN
				RAISE EXCEPTION 'The table % does not occur in the search_path', quote_literal($2);
				RETURN FALSE;
			END IF;
		END IF;

		rtn := _drop_overview_constraint(schema, $2, $3);
		IF rtn IS FALSE THEN
			RAISE EXCEPTION 'Unable to drop the overview constraint .  Is the schema name, table name or column name incorrect?';
			RETURN FALSE;
		END IF;

		RETURN TRUE;
	END;
	$_$;




CREATE FUNCTION public.droprasterconstraints(rasttable name, rastcolumn name, VARIADIC constraints text[]) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT DropRasterConstraints('', $1, $2, VARIADIC $3) $_$;




CREATE FUNCTION public.droprasterconstraints(rastschema name, rasttable name, rastcolumn name, VARIADIC constraints text[]) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		max int;
		x int;
		schema name;
		sql text;
		kw text;
		rtn boolean;
		cnt int;
	BEGIN
		cnt := 0;
		max := array_length(constraints, 1);
		IF max < 1 THEN
			RAISE NOTICE 'No constraints indicated to be dropped.  Doing nothing';
			RETURN TRUE;
		END IF;

		schema := NULL;
		IF length($1) > 0 THEN
			sql := 'SELECT nspname FROM pg_namespace '
				|| 'WHERE nspname = ' || quote_literal($1)
				|| 'LIMIT 1';
			EXECUTE sql INTO schema;

			IF schema IS NULL THEN
				RAISE EXCEPTION 'The value provided for schema is invalid';
				RETURN FALSE;
			END IF;
		END IF;

		IF schema IS NULL THEN
			sql := 'SELECT n.nspname AS schemaname '
				|| 'FROM pg_catalog.pg_class c '
				|| 'JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace '
				|| 'WHERE c.relkind = ' || quote_literal('r')
				|| ' AND n.nspname NOT IN (' || quote_literal('pg_catalog')
				|| ', ' || quote_literal('pg_toast')
				|| ') AND pg_catalog.pg_table_is_visible(c.oid)'
				|| ' AND c.relname = ' || quote_literal($2);
			EXECUTE sql INTO schema;

			IF schema IS NULL THEN
				RAISE EXCEPTION 'The table % does not occur in the search_path', quote_literal($2);
				RETURN FALSE;
			END IF;
		END IF;

		<<kwloop>>
		FOR x in 1..max LOOP
			kw := trim(both from lower(constraints[x]));

			BEGIN
				CASE
					WHEN kw = 'srid' THEN
						RAISE NOTICE 'Dropping SRID constraint';
						rtn := _drop_raster_constraint_srid(schema, $2, $3);
					WHEN kw IN ('scale_x', 'scalex') THEN
						RAISE NOTICE 'Dropping scale-X constraint';
						rtn := _drop_raster_constraint_scale(schema, $2, $3, 'x');
					WHEN kw IN ('scale_y', 'scaley') THEN
						RAISE NOTICE 'Dropping scale-Y constraint';
						rtn := _drop_raster_constraint_scale(schema, $2, $3, 'y');
					WHEN kw = 'scale' THEN
						RAISE NOTICE 'Dropping scale-X constraint';
						rtn := _drop_raster_constraint_scale(schema, $2, $3, 'x');
						RAISE NOTICE 'Dropping scale-Y constraint';
						rtn := _drop_raster_constraint_scale(schema, $2, $3, 'y');
					WHEN kw IN ('blocksize_x', 'blocksizex', 'width') THEN
						RAISE NOTICE 'Dropping blocksize-X constraint';
						rtn := _drop_raster_constraint_blocksize(schema, $2, $3, 'width');
					WHEN kw IN ('blocksize_y', 'blocksizey', 'height') THEN
						RAISE NOTICE 'Dropping blocksize-Y constraint';
						rtn := _drop_raster_constraint_blocksize(schema, $2, $3, 'height');
					WHEN kw = 'blocksize' THEN
						RAISE NOTICE 'Dropping blocksize-X constraint';
						rtn := _drop_raster_constraint_blocksize(schema, $2, $3, 'width');
						RAISE NOTICE 'Dropping blocksize-Y constraint';
						rtn := _drop_raster_constraint_blocksize(schema, $2, $3, 'height');
					WHEN kw IN ('same_alignment', 'samealignment', 'alignment') THEN
						RAISE NOTICE 'Dropping alignment constraint';
						rtn := _drop_raster_constraint_alignment(schema, $2, $3);
					WHEN kw IN ('regular_blocking', 'regularblocking') THEN
						RAISE NOTICE 'Dropping regular blocking constraint';
						rtn := _drop_raster_constraint_regular_blocking(schema, $2, $3);
					WHEN kw IN ('num_bands', 'numbands') THEN
						RAISE NOTICE 'Dropping number of bands constraint';
						rtn := _drop_raster_constraint_num_bands(schema, $2, $3);
					WHEN kw IN ('pixel_types', 'pixeltypes') THEN
						RAISE NOTICE 'Dropping pixel type constraint';
						rtn := _drop_raster_constraint_pixel_types(schema, $2, $3);
					WHEN kw IN ('nodata_values', 'nodatavalues', 'nodata') THEN
						RAISE NOTICE 'Dropping nodata value constraint';
						rtn := _drop_raster_constraint_nodata_values(schema, $2, $3);
					WHEN kw IN ('out_db', 'outdb') THEN
						RAISE NOTICE 'Dropping out-of-database constraint';
						rtn := _drop_raster_constraint_out_db(schema, $2, $3);
					WHEN kw = 'extent' THEN
						RAISE NOTICE 'Dropping maximum extent constraint';
						rtn := _drop_raster_constraint_extent(schema, $2, $3);
					ELSE
						RAISE NOTICE 'Unknown constraint: %.  Skipping', quote_literal(constraints[x]);
						CONTINUE kwloop;
				END CASE;
			END;

			IF rtn IS FALSE THEN
				cnt := cnt + 1;
				RAISE WARNING 'Unable to drop constraint: %.  Skipping', quote_literal(constraints[x]);
			END IF;

		END LOOP kwloop;

		IF cnt = max THEN
			RAISE EXCEPTION 'None of the constraints specified could be dropped.  Is the schema name, table name or column name incorrect?';
			RETURN FALSE;
		END IF;

		RETURN TRUE;
	END;
	$_$;




COMMENT ON FUNCTION public.droprasterconstraints(rastschema name, rasttable name, rastcolumn name, VARIADIC constraints text[]) IS 'args: rastschema, rasttable, rastcolumn, constraints - Drops PostGIS raster constraints that refer to a raster table column. Useful if you need to reload data or update your raster column data.';



CREATE FUNCTION public.droprasterconstraints(rasttable name, rastcolumn name, srid boolean DEFAULT true, scale_x boolean DEFAULT true, scale_y boolean DEFAULT true, blocksize_x boolean DEFAULT true, blocksize_y boolean DEFAULT true, same_alignment boolean DEFAULT true, regular_blocking boolean DEFAULT true, num_bands boolean DEFAULT true, pixel_types boolean DEFAULT true, nodata_values boolean DEFAULT true, out_db boolean DEFAULT true, extent boolean DEFAULT true) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT DropRasterConstraints('', $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14) $_$;




COMMENT ON FUNCTION public.droprasterconstraints(rasttable name, rastcolumn name, srid boolean, scale_x boolean, scale_y boolean, blocksize_x boolean, blocksize_y boolean, same_alignment boolean, regular_blocking boolean, num_bands boolean, pixel_types boolean, nodata_values boolean, out_db boolean, extent boolean) IS 'args: rasttable, rastcolumn, srid, scale_x, scale_y, blocksize_x, blocksize_y, same_alignment, regular_blocking, num_bands=true, pixel_types=true, nodata_values=true, out_db=true, extent=true - Drops PostGIS raster constraints that refer to a raster table column. Useful if you need to reload data or update your raster column data.';



CREATE FUNCTION public.droprasterconstraints(rastschema name, rasttable name, rastcolumn name, srid boolean DEFAULT true, scale_x boolean DEFAULT true, scale_y boolean DEFAULT true, blocksize_x boolean DEFAULT true, blocksize_y boolean DEFAULT true, same_alignment boolean DEFAULT true, regular_blocking boolean DEFAULT true, num_bands boolean DEFAULT true, pixel_types boolean DEFAULT true, nodata_values boolean DEFAULT true, out_db boolean DEFAULT true, extent boolean DEFAULT true) RETURNS boolean
    LANGUAGE plpgsql STRICT
    AS $_$
	DECLARE
		constraints text[];
	BEGIN
		IF srid IS TRUE THEN
			constraints := constraints || 'srid'::text;
		END IF;

		IF scale_x IS TRUE THEN
			constraints := constraints || 'scale_x'::text;
		END IF;

		IF scale_y IS TRUE THEN
			constraints := constraints || 'scale_y'::text;
		END IF;

		IF blocksize_x IS TRUE THEN
			constraints := constraints || 'blocksize_x'::text;
		END IF;

		IF blocksize_y IS TRUE THEN
			constraints := constraints || 'blocksize_y'::text;
		END IF;

		IF same_alignment IS TRUE THEN
			constraints := constraints || 'same_alignment'::text;
		END IF;

		IF regular_blocking IS TRUE THEN
			constraints := constraints || 'regular_blocking'::text;
		END IF;

		IF num_bands IS TRUE THEN
			constraints := constraints || 'num_bands'::text;
		END IF;

		IF pixel_types IS TRUE THEN
			constraints := constraints || 'pixel_types'::text;
		END IF;

		IF nodata_values IS TRUE THEN
			constraints := constraints || 'nodata_values'::text;
		END IF;

		IF out_db IS TRUE THEN
			constraints := constraints || 'out_db'::text;
		END IF;

		IF extent IS TRUE THEN
			constraints := constraints || 'extent'::text;
		END IF;

		RETURN DropRasterConstraints($1, $2, $3, VARIADIC constraints);
	END;
	$_$;




COMMENT ON FUNCTION public.droprasterconstraints(rastschema name, rasttable name, rastcolumn name, srid boolean, scale_x boolean, scale_y boolean, blocksize_x boolean, blocksize_y boolean, same_alignment boolean, regular_blocking boolean, num_bands boolean, pixel_types boolean, nodata_values boolean, out_db boolean, extent boolean) IS 'args: rastschema, rasttable, rastcolumn, srid=true, scale_x=true, scale_y=true, blocksize_x=true, blocksize_y=true, same_alignment=true, regular_blocking=true, num_bands=true, pixel_types=true, nodata_values=true, out_db=true, extent=true - Drops PostGIS raster constraints that refer to a raster table column. Useful if you need to reload data or update your raster column data.';



CREATE FUNCTION public.enablelongtransactions() RETURNS text
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	"query" text;
	exists bool;
	rec RECORD;

BEGIN

	exists = 'f';
	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorization_table'
	LOOP
		exists = 't';
	END LOOP;

	IF NOT exists
	THEN
		"query" = 'CREATE TABLE authorization_table (
			expires timestamp,
			authid text
		)';
		EXECUTE "query";
	END IF;

	exists = 'f';
	FOR rec IN SELECT * FROM pg_class WHERE relname = 'authorized_tables'
	LOOP
		exists = 't';
	END LOOP;

	IF NOT exists THEN
		"query" = 'CREATE VIEW authorized_tables AS ' ||
			'SELECT ' ||
			'n.nspname as schema, ' ||
			'c.relname as table, trim(' ||
			quote_literal(chr(92) || '000') ||
			' from t.tgargs) as id_column ' ||
			'FROM pg_trigger t, pg_class c, pg_proc p ' ||
			', pg_namespace n ' ||
			'WHERE p.proname = ' || quote_literal('checkauthtrigger') ||
			' AND c.relnamespace = n.oid' ||
			' AND t.tgfoid = p.oid and t.tgrelid = c.oid';
		EXECUTE "query";
	END IF;

	RETURN 'Long transactions support enabled';
END;
$$;




CREATE FUNCTION public.find_srid(character varying, character varying, character varying) RETURNS integer
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
DECLARE
	schem text;
	tabl text;
	sr int4;
BEGIN
	IF $1 IS NULL THEN
	  RAISE EXCEPTION 'find_srid() - schema is NULL!';
	END IF;
	IF $2 IS NULL THEN
	  RAISE EXCEPTION 'find_srid() - table name is NULL!';
	END IF;
	IF $3 IS NULL THEN
	  RAISE EXCEPTION 'find_srid() - column name is NULL!';
	END IF;
	schem = $1;
	tabl = $2;
	IF ( schem = '' and tabl LIKE '%.%' ) THEN
	 schem = substr(tabl,1,strpos(tabl,'.')-1);
	 tabl = substr(tabl,length(schem)+2);
	ELSE
	 schem = schem || '%';
	END IF;

	select SRID into sr from geometry_columns where f_table_schema like schem and f_table_name = tabl and f_geometry_column = $3;
	IF NOT FOUND THEN
	   RAISE EXCEPTION 'find_srid() - couldnt find the corresponding SRID - is the geometry registered in the GEOMETRY_COLUMNS table?  Is there an uppercase/lowercase missmatch?';
	END IF;
	return sr;
END;
$_$;




CREATE FUNCTION public.get_proj4_from_srid(integer) RETURNS text
    LANGUAGE plpgsql IMMUTABLE STRICT
    AS $_$
BEGIN
	RETURN proj4text::text FROM spatial_ref_sys WHERE srid= $1;
END;
$_$;




CREATE FUNCTION public.lockrow(text, text, text) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow(current_schema(), $1, $2, $3, now()::timestamp+'1:00'); $_$;




CREATE FUNCTION public.lockrow(text, text, text, text) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow($1, $2, $3, $4, now()::timestamp+'1:00'); $_$;




CREATE FUNCTION public.lockrow(text, text, text, timestamp without time zone) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow(current_schema(), $1, $2, $3, $4); $_$;




CREATE FUNCTION public.lockrow(text, text, text, text, timestamp without time zone) RETURNS integer
    LANGUAGE plpgsql STRICT
    AS $_$ 
DECLARE
	myschema alias for $1;
	mytable alias for $2;
	myrid   alias for $3;
	authid alias for $4;
	expires alias for $5;
	ret int;
	mytoid oid;
	myrec RECORD;
	
BEGIN

	IF NOT LongTransactionsEnabled() THEN
		RAISE EXCEPTION 'Long transaction support disabled, use EnableLongTransaction() to enable.';
	END IF;

	EXECUTE 'DELETE FROM authorization_table WHERE expires < now()'; 

	SELECT c.oid INTO mytoid FROM pg_class c, pg_namespace n
		WHERE c.relname = mytable
		AND c.relnamespace = n.oid
		AND n.nspname = myschema;


	FOR myrec IN SELECT * FROM authorization_table WHERE 
		toid = mytoid AND rid = myrid
	LOOP
		IF myrec.authid != authid THEN
			RETURN 0;
		ELSE
			RETURN 1;
		END IF;
	END LOOP;

	EXECUTE 'INSERT INTO authorization_table VALUES ('||
		quote_literal(mytoid::text)||','||quote_literal(myrid)||
		','||quote_literal(expires::text)||
		','||quote_literal(authid) ||')';

	GET DIAGNOSTICS ret = ROW_COUNT;

	RETURN ret;
END;
$_$;




CREATE FUNCTION public.longtransactionsenabled() RETURNS boolean
    LANGUAGE plpgsql
    AS $$ 
DECLARE
	rec RECORD;
BEGIN
	FOR rec IN SELECT oid FROM pg_class WHERE relname = 'authorized_tables'
	LOOP
		return 't';
	END LOOP;
	return 'f';
END;
$$;




CREATE FUNCTION public.populate_geometry_columns(use_typmod boolean DEFAULT true) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
	inserted    integer;
	oldcount    integer;
	probed      integer;
	stale       integer;
	gcs         RECORD;
	gc          RECORD;
	gsrid       integer;
	gndims      integer;
	gtype       text;
	query       text;
	gc_is_valid boolean;

BEGIN
	SELECT count(*) INTO oldcount FROM geometry_columns;
	inserted := 0;

	SELECT count(DISTINCT c.oid) INTO probed
	FROM pg_class c,
		 pg_attribute a,
		 pg_type t,
		 pg_namespace n
	WHERE (c.relkind = 'r' OR c.relkind = 'v')
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND n.nspname NOT ILIKE 'pg_temp%' AND c.relname != 'raster_columns' ;

	RAISE DEBUG 'Processing Tables.....';

	FOR gcs IN
	SELECT DISTINCT ON (c.oid) c.oid, n.nspname, c.relname
		FROM pg_class c,
			 pg_attribute a,
			 pg_type t,
			 pg_namespace n
		WHERE c.relkind = 'r'
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND n.nspname NOT ILIKE 'pg_temp%' AND c.relname != 'raster_columns' 
	LOOP

		inserted := inserted + populate_geometry_columns(gcs.oid, use_typmod);
	END LOOP;

	IF oldcount > inserted THEN
	    stale = oldcount-inserted;
	ELSE
	    stale = 0;
	END IF;

	RETURN 'probed:' ||probed|| ' inserted:'||inserted;
END

$$;




CREATE FUNCTION public.populate_geometry_columns(tbl_oid oid, use_typmod boolean DEFAULT true) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE
	gcs         RECORD;
	gc          RECORD;
	gc_old      RECORD;
	gsrid       integer;
	gndims      integer;
	gtype       text;
	query       text;
	gc_is_valid boolean;
	inserted    integer;
	constraint_successful boolean := false;

BEGIN
	inserted := 0;

	FOR gcs IN
	SELECT n.nspname, c.relname, a.attname
		FROM pg_class c,
			 pg_attribute a,
			 pg_type t,
			 pg_namespace n
		WHERE c.relkind = 'r'
		AND t.typname = 'geometry'
		AND a.attisdropped = false
		AND a.atttypid = t.oid
		AND a.attrelid = c.oid
		AND c.relnamespace = n.oid
		AND n.nspname NOT ILIKE 'pg_temp%'
		AND c.oid = tbl_oid
	LOOP

        RAISE DEBUG 'Processing table %.%.%', gcs.nspname, gcs.relname, gcs.attname;
    
        gc_is_valid := true;
        
        SELECT type, srid, coord_dimension INTO gc_old 
            FROM geometry_columns 
            WHERE f_table_schema = gcs.nspname AND f_table_name = gcs.relname AND f_geometry_column = gcs.attname; 
            
        IF upper(gc_old.type) = 'GEOMETRY' THEN
            EXECUTE 'SELECT st_srid(' || quote_ident(gcs.attname) || ') As srid, GeometryType(' || quote_ident(gcs.attname) || ') As type, ST_NDims(' || quote_ident(gcs.attname) || ') As dims ' ||
                     ' FROM ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || 
                     ' WHERE ' || quote_ident(gcs.attname) || ' IS NOT NULL LIMIT 1;'
                INTO gc;
            	RAISE WARNING 'No data in table %.%, so no information to determine geometry type and srid', gcs.nspname, gcs.relname;
            	RETURN 0;
            END IF;
            gsrid := gc.srid; gtype := gc.type; gndims := gc.dims;
            	
            IF use_typmod THEN
                BEGIN
                    EXECUTE 'ALTER TABLE ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || ' ALTER COLUMN ' || quote_ident(gcs.attname) || 
                        ' TYPE geometry(' || postgis_type_name(gtype, gndims, true) || ', ' || gsrid::text  || ') ';
                    inserted := inserted + 1;
                EXCEPTION
                        WHEN invalid_parameter_value THEN
                        RAISE WARNING 'Could not convert ''%'' in ''%.%'' to use typmod with srid %, type: % ', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname), gsrid, postgis_type_name(gtype, gndims, true);
                            gc_is_valid := false;
                END;
                
            ELSE
            	constraint_successful = false;
                IF (gsrid > 0 AND postgis_constraint_srid(gcs.nspname, gcs.relname,gcs.attname) IS NULL ) THEN
                    BEGIN
                        EXECUTE 'ALTER TABLE ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || 
                                 ' ADD CONSTRAINT ' || quote_ident('enforce_srid_' || gcs.attname) || 
                                 ' CHECK (st_srid(' || quote_ident(gcs.attname) || ') = ' || gsrid || ')';
                        constraint_successful := true;
                    EXCEPTION
                        WHEN check_violation THEN
                            RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not apply constraint CHECK (st_srid(%) = %)', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname), quote_ident(gcs.attname), gsrid;
                            gc_is_valid := false;
                    END;
                END IF;
                
                IF (gndims IS NOT NULL AND postgis_constraint_dims(gcs.nspname, gcs.relname,gcs.attname) IS NULL ) THEN
                    BEGIN
                        EXECUTE 'ALTER TABLE ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
                                 ADD CONSTRAINT ' || quote_ident('enforce_dims_' || gcs.attname) || '
                                 CHECK (st_ndims(' || quote_ident(gcs.attname) || ') = '||gndims||')';
                        constraint_successful := true;
                    EXCEPTION
                        WHEN check_violation THEN
                            RAISE WARNING 'Not inserting ''%'' in ''%.%'' into geometry_columns: could not apply constraint CHECK (st_ndims(%) = %)', quote_ident(gcs.attname), quote_ident(gcs.nspname), quote_ident(gcs.relname), quote_ident(gcs.attname), gndims;
                            gc_is_valid := false;
                    END;
                END IF;
    
                IF (gtype IS NOT NULL AND postgis_constraint_type(gcs.nspname, gcs.relname,gcs.attname) IS NULL ) THEN
                    BEGIN
                        EXECUTE 'ALTER TABLE ONLY ' || quote_ident(gcs.nspname) || '.' || quote_ident(gcs.relname) || '
                        ADD CONSTRAINT ' || quote_ident('enforce_geotype_' || gcs.attname) || '
                        CHECK ((geometrytype(' || quote_ident(gcs.attname) || ') = ' || quote_literal(gtype) || ') OR (' || quote_ident(gcs.attname) || ' IS NULL))';
                        constraint_successful := true;
                    EXCEPTION
                        WHEN check_violation THEN
                            RAISE WARNING 'Could not add geometry type check (%) to table column: %.%.%', gtype, quote_ident(gcs.nspname),quote_ident(gcs.relname),quote_ident(gcs.attname);
                    END;
                END IF;
                IF constraint_successful THEN
                	inserted := inserted + 1;
                END IF;
            END IF;	        
	    END IF;

	END LOOP;

	RETURN inserted;
END

$$;




CREATE FUNCTION public.postgis_constraint_dims(geomschema text, geomtable text, geomcolumn text) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$
SELECT  replace(split_part(s.consrc, ' = ', 2), ')', '')::integer
		 FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
		 WHERE n.nspname = $1
		 AND c.relname = $2
		 AND a.attname = $3
		 AND a.attrelid = c.oid
		 AND s.connamespace = n.oid
		 AND s.conrelid = c.oid
		 AND a.attnum = ANY (s.conkey)
		 AND s.consrc LIKE '%ndims(% = %';
$_$;




CREATE FUNCTION public.postgis_constraint_srid(geomschema text, geomtable text, geomcolumn text) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$
SELECT replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', '')::integer
		 FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
		 WHERE n.nspname = $1
		 AND c.relname = $2
		 AND a.attname = $3
		 AND a.attrelid = c.oid
		 AND s.connamespace = n.oid
		 AND s.conrelid = c.oid
		 AND a.attnum = ANY (s.conkey)
		 AND s.consrc LIKE '%srid(% = %';
$_$;




CREATE FUNCTION public.postgis_constraint_type(geomschema text, geomtable text, geomcolumn text) RETURNS character varying
    LANGUAGE sql STABLE STRICT
    AS $_$
SELECT  replace(split_part(s.consrc, '''', 2), ')', '')::varchar		
		 FROM pg_class c, pg_namespace n, pg_attribute a, pg_constraint s
		 WHERE n.nspname = $1
		 AND c.relname = $2
		 AND a.attname = $3
		 AND a.attrelid = c.oid
		 AND s.connamespace = n.oid
		 AND s.conrelid = c.oid
		 AND a.attnum = ANY (s.conkey)
		 AND s.consrc LIKE '%geometrytype(% = %';
$_$;




CREATE FUNCTION public.postgis_full_version() RETURNS text
    LANGUAGE plpgsql IMMUTABLE
    AS $$
DECLARE
	libver text;
	svnver text;
	projver text;
	geosver text;
	gdalver text;
	libxmlver text;
	dbproc text;
	relproc text;
	fullver text;
	rast_lib_ver text;
	rast_scr_ver text;
	topo_scr_ver text;
	json_lib_ver text;
BEGIN
	SELECT postgis_lib_version() INTO libver;
	SELECT postgis_proj_version() INTO projver;
	SELECT postgis_geos_version() INTO geosver;
	SELECT postgis_libjson_version() INTO json_lib_ver;
	BEGIN
		SELECT postgis_gdal_version() INTO gdalver;
	EXCEPTION
		WHEN undefined_function THEN
			gdalver := NULL;
			RAISE NOTICE 'Function postgis_gdal_version() not found.  Is raster support enabled and rtpostgis.sql installed?';
	END;
	SELECT postgis_libxml_version() INTO libxmlver;
	SELECT postgis_scripts_installed() INTO dbproc;
	SELECT postgis_scripts_released() INTO relproc;
	select postgis_svn_version() INTO svnver;
	BEGIN
		SELECT postgis_topology_scripts_installed() INTO topo_scr_ver;
	EXCEPTION
		WHEN undefined_function THEN
			topo_scr_ver := NULL;
			RAISE NOTICE 'Function postgis_topology_scripts_installed() not found. Is topology support enabled and topology.sql installed?';
	END;

	BEGIN
		SELECT postgis_raster_scripts_installed() INTO rast_scr_ver;
	EXCEPTION
		WHEN undefined_function THEN
			rast_scr_ver := NULL;
			RAISE NOTICE 'Function postgis_raster_scripts_installed() not found. Is raster support enabled and rtpostgis.sql installed?';
	END;

	BEGIN
		SELECT postgis_raster_lib_version() INTO rast_lib_ver;
	EXCEPTION
		WHEN undefined_function THEN
			rast_lib_ver := NULL;
			RAISE NOTICE 'Function postgis_raster_lib_version() not found. Is raster support enabled and rtpostgis.sql installed?';
	END;

	fullver = 'POSTGIS="' || libver;

	IF  svnver IS NOT NULL THEN
		fullver = fullver || ' r' || svnver;
	END IF;

	fullver = fullver || '"';

	IF  geosver IS NOT NULL THEN
		fullver = fullver || ' GEOS="' || geosver || '"';
	END IF;

	IF  projver IS NOT NULL THEN
		fullver = fullver || ' PROJ="' || projver || '"';
	END IF;

	IF  gdalver IS NOT NULL THEN
		fullver = fullver || ' GDAL="' || gdalver || '"';
	END IF;

	IF  libxmlver IS NOT NULL THEN
		fullver = fullver || ' LIBXML="' || libxmlver || '"';
	END IF;

	IF json_lib_ver IS NOT NULL THEN
		fullver = fullver || ' LIBJSON="' || json_lib_ver || '"';
	END IF;


	IF dbproc != relproc THEN
		fullver = fullver || ' (core procs from "' || dbproc || '" need upgrade)';
	END IF;

	IF topo_scr_ver IS NOT NULL THEN
		fullver = fullver || ' TOPOLOGY';
		IF topo_scr_ver != relproc THEN
			fullver = fullver || ' (topology procs from "' || topo_scr_ver || '" need upgrade)';
		END IF;
	END IF;

	IF rast_lib_ver IS NOT NULL THEN
		fullver = fullver || ' RASTER';
		IF rast_lib_ver != relproc THEN
			fullver = fullver || ' (raster lib from "' || rast_lib_ver || '" need upgrade)';
		END IF;
	END IF;

	IF rast_scr_ver IS NOT NULL AND rast_scr_ver != relproc THEN
		fullver = fullver || ' (raster procs from "' || rast_scr_ver || '" need upgrade)';
	END IF;

	RETURN fullver;
END
$$;




CREATE FUNCTION public.postgis_raster_scripts_installed() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$ SELECT '2.0.1'::text || ' r' || 9979::text AS version $$;




CREATE FUNCTION public.postgis_scripts_build_date() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '2012-11-16 18:39:39'::text AS version$$;




CREATE FUNCTION public.postgis_scripts_installed() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$ SELECT '2.0.1'::text || ' r' || 9979::text AS version $$;




CREATE FUNCTION public.postgis_topology_scripts_installed() RETURNS text
    LANGUAGE sql IMMUTABLE
    AS $$ SELECT '2.0.1'::text || ' r' || 9979::text AS version $$;




CREATE FUNCTION public.postgis_type_name(geomname character varying, coord_dimension integer, use_new_name boolean DEFAULT true) RETURNS character varying
    LANGUAGE sql IMMUTABLE STRICT COST 200
    AS $_$
 SELECT CASE WHEN $3 THEN new_name ELSE old_name END As geomname
 	FROM 
 	( VALUES
 		 ('GEOMETRY', 'Geometry', 2) ,
 		 	('GEOMETRY', 'GeometryZ', 3) ,
 		 	('GEOMETRY', 'GeometryZM', 4) ,
			('GEOMETRYCOLLECTION', 'GeometryCollection', 2) ,
			('GEOMETRYCOLLECTION', 'GeometryCollectionZ', 3) ,
			('GEOMETRYCOLLECTIONM', 'GeometryCollectionM', 3) ,
			('GEOMETRYCOLLECTION', 'GeometryCollectionZM', 4) ,
			
			('POINT', 'Point',2) ,
			('POINTM','PointM',3) ,
			('POINT', 'PointZ',3) ,
			('POINT', 'PointZM',4) ,
			
			('MULTIPOINT','MultiPoint',2) ,
			('MULTIPOINT','MultiPointZ',3) ,
			('MULTIPOINTM','MultiPointM',3) ,
			('MULTIPOINT','MultiPointZM',4) ,
			
			('POLYGON', 'Polygon',2) ,
			('POLYGON', 'PolygonZ',3) ,
			('POLYGONM', 'PolygonM',3) ,
			('POLYGON', 'PolygonZM',4) ,
			
			('MULTIPOLYGON', 'MultiPolygon',2) ,
			('MULTIPOLYGON', 'MultiPolygonZ',3) ,
			('MULTIPOLYGONM', 'MultiPolygonM',3) ,
			('MULTIPOLYGON', 'MultiPolygonZM',4) ,
			
			('MULTILINESTRING', 'MultiLineString',2) ,
			('MULTILINESTRING', 'MultiLineStringZ',3) ,
			('MULTILINESTRINGM', 'MultiLineStringM',3) ,
			('MULTILINESTRING', 'MultiLineStringZM',4) ,
			
			('LINESTRING', 'LineString',2) ,
			('LINESTRING', 'LineStringZ',3) ,
			('LINESTRINGM', 'LineStringM',3) ,
			('LINESTRING', 'LineStringZM',4) ,
			
			('CIRCULARSTRING', 'CircularString',2) ,
			('CIRCULARSTRING', 'CircularStringZ',3) ,
			('CIRCULARSTRINGM', 'CircularStringM',3) ,
			('CIRCULARSTRING', 'CircularStringZM',4) ,
			
			('COMPOUNDCURVE', 'CompoundCurve',2) ,
			('COMPOUNDCURVE', 'CompoundCurveZ',3) ,
			('COMPOUNDCURVEM', 'CompoundCurveM',3) ,
			('COMPOUNDCURVE', 'CompoundCurveZM',4) ,
			
			('CURVEPOLYGON', 'CurvePolygon',2) ,
			('CURVEPOLYGON', 'CurvePolygonZ',3) ,
			('CURVEPOLYGONM', 'CurvePolygonM',3) ,
			('CURVEPOLYGON', 'CurvePolygonZM',4) ,
			
			('MULTICURVE', 'MultiCurve',2 ) ,
			('MULTICURVE', 'MultiCurveZ',3 ) ,
			('MULTICURVEM', 'MultiCurveM',3 ) ,
			('MULTICURVE', 'MultiCurveZM',4 ) ,
			
			('MULTISURFACE', 'MultiSurface', 2) ,
			('MULTISURFACE', 'MultiSurfaceZ', 3) ,
			('MULTISURFACEM', 'MultiSurfaceM', 3) ,
			('MULTISURFACE', 'MultiSurfaceZM', 4) ,
			
			('POLYHEDRALSURFACE', 'PolyhedralSurface',2) ,
			('POLYHEDRALSURFACE', 'PolyhedralSurfaceZ',3) ,
			('POLYHEDRALSURFACEM', 'PolyhedralSurfaceM',3) ,
			('POLYHEDRALSURFACE', 'PolyhedralSurfaceZM',4) ,
			
			('TRIANGLE', 'Triangle',2) ,
			('TRIANGLE', 'TriangleZ',3) ,
			('TRIANGLEM', 'TriangleM',3) ,
			('TRIANGLE', 'TriangleZM',4) ,

			('TIN', 'Tin', 2),
			('TIN', 'TinZ', 3),
			('TIN', 'TinM', 3),
			('TIN', 'TinZM', 4) )
			 As g(old_name, new_name, coord_dimension)
		WHERE (upper(old_name) = upper($1) OR upper(new_name) = upper($1))
			AND coord_dimension = $2;
$_$;




CREATE FUNCTION public.st_approxcount(rastertable text, rastercolumn text, sample_percent double precision) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, 1, TRUE, $3) $_$;




CREATE FUNCTION public.st_approxcount(rastertable text, rastercolumn text, exclude_nodata_value boolean, sample_percent double precision DEFAULT 0.1) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, 1, $3, $4) $_$;




CREATE FUNCTION public.st_approxcount(rastertable text, rastercolumn text, nband integer, sample_percent double precision) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, $3, TRUE, $4) $_$;




CREATE FUNCTION public.st_approxcount(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, $3, $4, $5) $_$;




CREATE FUNCTION public.st_approxhistogram(rastertable text, rastercolumn text, sample_percent double precision) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, 1, TRUE, $3, 0, NULL, FALSE) $_$;




CREATE FUNCTION public.st_approxhistogram(rastertable text, rastercolumn text, nband integer, sample_percent double precision) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, $4, 0, NULL, FALSE) $_$;




CREATE FUNCTION public.st_approxhistogram(rastertable text, rastercolumn text, nband integer, sample_percent double precision, bins integer, "right" boolean) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, $4, $5, NULL, $6) $_$;




CREATE FUNCTION public.st_approxhistogram(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, sample_percent double precision, bins integer, "right" boolean) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, $5, $6, NULL, $7) $_$;




CREATE FUNCTION public.st_approxhistogram(rastertable text, rastercolumn text, nband integer, sample_percent double precision, bins integer, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, $4, $5, $6, $7) $_$;




CREATE FUNCTION public.st_approxhistogram(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1, bins integer DEFAULT 0, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, $5, $6, $7, $8) $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, quantiles double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_quantile($1, $2, 1, TRUE, 0.1, $3) $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE
    AS $_$ SELECT (_st_quantile($1, $2, 1, TRUE, 0.1, ARRAY[$3]::double precision[])).value $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, exclude_nodata_value boolean, quantile double precision DEFAULT NULL::double precision) RETURNS double precision
    LANGUAGE sql STABLE
    AS $_$ SELECT (_st_quantile($1, $2, 1, $3, 0.1, ARRAY[$4]::double precision[])).value $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, sample_percent double precision, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, 1, TRUE, $3, $4) $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, sample_percent double precision, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, 1, TRUE, $3, ARRAY[$4]::double precision[])).value $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, nband integer, sample_percent double precision, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, $3, TRUE, $4, $5) $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, nband integer, sample_percent double precision, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, TRUE, $4, ARRAY[$5]::double precision[])).value $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, $3, $4, $5, $6) $_$;




CREATE FUNCTION public.st_approxquantile(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, sample_percent double precision, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, $4, $5, ARRAY[$6]::double precision[])).value $_$;




CREATE FUNCTION public.st_approxsummarystats(rastertable text, rastercolumn text, exclude_nodata_value boolean) RETURNS public.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, 1, $3, 0.1) $_$;




CREATE FUNCTION public.st_approxsummarystats(rastertable text, rastercolumn text, sample_percent double precision) RETURNS public.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, 1, TRUE, $3) $_$;




CREATE FUNCTION public.st_approxsummarystats(rastertable text, rastercolumn text, nband integer, sample_percent double precision) RETURNS public.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, $3, TRUE, $4) $_$;




CREATE FUNCTION public.st_approxsummarystats(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1) RETURNS public.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, $3, $4, $5) $_$;




CREATE FUNCTION public.st_area(text) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Area($1::geometry);  $_$;




CREATE FUNCTION public.st_asewkt(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsEWKT($1::geometry);  $_$;




CREATE FUNCTION public.st_asgeojson(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_AsGeoJson(1, $1::geometry,15,0);  $_$;




CREATE FUNCTION public.st_asgml(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_AsGML(2,$1::geometry,15,0, NULL);  $_$;




CREATE FUNCTION public.st_askml(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_AsKML(2, $1::geometry, 15, null);  $_$;




CREATE FUNCTION public.st_assvg(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsSVG($1::geometry,0,15);  $_$;




CREATE FUNCTION public.st_astext(text) RETURNS text
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsText($1::geometry);  $_$;




CREATE FUNCTION public.st_count(rastertable text, rastercolumn text, exclude_nodata_value boolean) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, 1, $3, 1) $_$;




COMMENT ON FUNCTION public.st_count(rastertable text, rastercolumn text, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, exclude_nodata_value - Returns the number of pixels in a given band of a raster or raster coverage. If no band is specified defaults to band 1. If exclude_nodata_value is set to true, will only count pixels that are not equal to the nodata value.';



CREATE FUNCTION public.st_count(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, $3, $4, 1) $_$;




COMMENT ON FUNCTION public.st_count(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true - Returns the number of pixels in a given band of a raster or raster coverage. If no band is specified defaults to band 1. If exclude_nodata_value is set to true, will only count pixels that are not equal to the nodata value.';



CREATE FUNCTION public.st_coveredby(text, text) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_CoveredBy($1::geometry, $2::geometry);  $_$;




CREATE FUNCTION public.st_covers(text, text) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_Covers($1::geometry, $2::geometry);  $_$;




CREATE FUNCTION public.st_distance(text, text) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Distance($1::geometry, $2::geometry);  $_$;




CREATE FUNCTION public.st_distinct4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT COUNT(DISTINCT unnest)::float FROM unnest($1) $_$;




COMMENT ON FUNCTION public.st_distinct4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the number of unique pixel values in a neighborhood.';



CREATE FUNCTION public.st_dwithin(text, text, double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_DWithin($1::geometry, $2::geometry, $3);  $_$;




CREATE FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, bins integer, "right" boolean) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, 1, $4, NULL, $5) $_$;




COMMENT ON FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, bins integer, "right" boolean) IS 'args: rastertable, rastercolumn, nband, bins, right - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';



CREATE FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, bins integer, "right" boolean) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, 1, $5, NULL, $6) $_$;




COMMENT ON FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, bins integer, "right" boolean) IS 'args: rastertable, rastercolumn, nband, exclude_nodata_value, bins, right - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';



CREATE FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, bins integer, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, 1, $4, $5, $6) $_$;




COMMENT ON FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, bins integer, width double precision[], "right" boolean) IS 'args: rastertable, rastercolumn, nband=1, bins, width=NULL, right=false - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';



CREATE FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, bins integer DEFAULT 0, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF public.histogram
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, 1, $5, $6, $7) $_$;




COMMENT ON FUNCTION public.st_histogram(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, bins integer, width double precision[], "right" boolean) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true, bins=autocomputed, width=NULL, right=false - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';



CREATE FUNCTION public.st_intersects(text, text) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_Intersects($1::geometry, $2::geometry);  $_$;




CREATE FUNCTION public.st_length(text) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Length($1::geometry);  $_$;




CREATE FUNCTION public.st_max4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        _matrix float[][];
        max float;
    BEGIN
        _matrix := matrix;
        max := '-Infinity'::float;
        FOR x in array_lower(_matrix, 1)..array_upper(_matrix, 1) LOOP
            FOR y in array_lower(_matrix, 2)..array_upper(_matrix, 2) LOOP
                IF _matrix[x][y] IS NULL THEN
                    IF NOT nodatamode = 'ignore' THEN
                        _matrix[x][y] := nodatamode::float;
                    END IF;
                END IF;
                IF max < _matrix[x][y] THEN
                    max := _matrix[x][y];
                END IF;
            END LOOP;
        END LOOP;
        RETURN max;
    END;
    $$;




COMMENT ON FUNCTION public.st_max4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the maximum pixel value in a neighborhood.';



CREATE FUNCTION public.st_mean4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        _matrix float[][];
        sum float;
        count float;
    BEGIN
        _matrix := matrix;
        sum := 0;
        count := 0;
        FOR x in array_lower(matrix, 1)..array_upper(matrix, 1) LOOP
            FOR y in array_lower(matrix, 2)..array_upper(matrix, 2) LOOP
                IF _matrix[x][y] IS NULL THEN
                    IF nodatamode = 'ignore' THEN
                        _matrix[x][y] := 0;
                    ELSE
                        _matrix[x][y] := nodatamode::float;
                        count := count + 1;
                    END IF;
                ELSE
                    count := count + 1;
                END IF;
                sum := sum + _matrix[x][y];
            END LOOP;
        END LOOP;
        IF count = 0 THEN
            RETURN NULL;
        END IF;
        RETURN sum / count;
    END;
    $$;




COMMENT ON FUNCTION public.st_mean4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the mean pixel value in a neighborhood.';



CREATE FUNCTION public.st_min4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        _matrix float[][];
        min float;
    BEGIN
        _matrix := matrix;
        min := 'Infinity'::float;
        FOR x in array_lower(_matrix, 1)..array_upper(_matrix, 1) LOOP
            FOR y in array_lower(_matrix, 2)..array_upper(_matrix, 2) LOOP
                IF _matrix[x][y] IS NULL THEN
                    IF NOT nodatamode = 'ignore' THEN
                        _matrix[x][y] := nodatamode::float;
                    END IF;
                END IF;
                IF min > _matrix[x][y] THEN
                    min := _matrix[x][y];
                END IF;
            END LOOP;
        END LOOP;
        RETURN min;
    END;
    $$;




COMMENT ON FUNCTION public.st_min4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the minimum pixel value in a neighborhood.';



CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, quantiles double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_quantile($1, $2, 1, TRUE, 1, $3) $_$;




CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, 1, TRUE, 1, ARRAY[$3]::double precision[])).value $_$;




CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, exclude_nodata_value boolean, quantile double precision DEFAULT NULL::double precision) RETURNS double precision
    LANGUAGE sql STABLE
    AS $_$ SELECT (_st_quantile($1, $2, 1, $3, 1, ARRAY[$4]::double precision[])).value $_$;




CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, nband integer, quantiles double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_quantile($1, $2, $3, TRUE, 1, $4) $_$;




COMMENT ON FUNCTION public.st_quantile(rastertable text, rastercolumn text, nband integer, quantiles double precision[]) IS 'args: rastertable, rastercolumn, nband, quantiles - Compute quantiles for a raster or raster table coverage in the context of the sample or population. Thus, a value could be examined to be at the rasters 25%, 50%, 75% percentile.';



CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, nband integer, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, TRUE, 1, ARRAY[$4]::double precision[])).value $_$;




CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF public.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, $3, $4, 1, $5) $_$;




COMMENT ON FUNCTION public.st_quantile(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, quantiles double precision[]) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true, quantiles=NULL - Compute quantiles for a raster or raster table coverage in the context of the sample or population. Thus, a value could be examined to be at the rasters 25%, 50%, 75% percentile.';



CREATE FUNCTION public.st_quantile(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, $4, 1, ARRAY[$5]::double precision[])).value $_$;




CREATE FUNCTION public.st_range4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        _matrix float[][];
        min float;
        max float;
    BEGIN
        _matrix := matrix;
        min := 'Infinity'::float;
        max := '-Infinity'::float;
        FOR x in array_lower(matrix, 1)..array_upper(matrix, 1) LOOP
            FOR y in array_lower(matrix, 2)..array_upper(matrix, 2) LOOP
                IF _matrix[x][y] IS NULL THEN
                    IF NOT nodatamode = 'ignore' THEN
                        _matrix[x][y] := nodatamode::float;
                    END IF;
                END IF;
                IF min > _matrix[x][y] THEN
                    min = _matrix[x][y];
                END IF;
                IF max < _matrix[x][y] THEN
                    max = _matrix[x][y];
                END IF;
            END LOOP;
        END LOOP;
        IF max = '-Infinity'::float OR min = 'Infinity'::float THEN
            RETURN NULL;
        END IF;
        RETURN max - min;
    END;
    $$;




COMMENT ON FUNCTION public.st_range4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the range of pixel values in a neighborhood.';



CREATE FUNCTION public.st_samealignment(ulx1 double precision, uly1 double precision, scalex1 double precision, scaley1 double precision, skewx1 double precision, skewy1 double precision, ulx2 double precision, uly2 double precision, scalex2 double precision, scaley2 double precision, skewx2 double precision, skewy2 double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT st_samealignment(st_makeemptyraster(1, 1, $1, $2, $3, $4, $5, $6), st_makeemptyraster(1, 1, $7, $8, $9, $10, $11, $12)) $_$;




COMMENT ON FUNCTION public.st_samealignment(ulx1 double precision, uly1 double precision, scalex1 double precision, scaley1 double precision, skewx1 double precision, skewy1 double precision, ulx2 double precision, uly2 double precision, scalex2 double precision, scaley2 double precision, skewx2 double precision, skewy2 double precision) IS 'args: ulx1, uly1, scalex1, scaley1, skewx1, skewy1, ulx2, uly2, scalex2, scaley2, skewx2, skewy2 - Returns true if rasters have same skew, scale, spatial ref and false if they dont with notice detailing issue.';



CREATE FUNCTION public.st_stddev4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT stddev(unnest) FROM unnest($1) $_$;




COMMENT ON FUNCTION public.st_stddev4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the standard deviation of pixel values in a neighborhood.';



CREATE FUNCTION public.st_sum4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) RETURNS double precision
    LANGUAGE plpgsql IMMUTABLE
    AS $$
    DECLARE
        _matrix float[][];
        sum float;
    BEGIN
        _matrix := matrix;
        sum := 0;
        FOR x in array_lower(matrix, 1)..array_upper(matrix, 1) LOOP
            FOR y in array_lower(matrix, 2)..array_upper(matrix, 2) LOOP
                IF _matrix[x][y] IS NULL THEN
                    IF nodatamode = 'ignore' THEN
                        _matrix[x][y] := 0;
                    ELSE
                        _matrix[x][y] := nodatamode::float;
                    END IF;
                END IF;
                sum := sum + _matrix[x][y];
            END LOOP;
        END LOOP;
        RETURN sum;
    END;
    $$;




COMMENT ON FUNCTION public.st_sum4ma(matrix double precision[], nodatamode text, VARIADIC args text[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the sum of all pixel values in a neighborhood.';



CREATE FUNCTION public.st_summarystats(rastertable text, rastercolumn text, exclude_nodata_value boolean) RETURNS public.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, 1, $3, 1) $_$;




COMMENT ON FUNCTION public.st_summarystats(rastertable text, rastercolumn text, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, exclude_nodata_value - Returns summary stats consisting of count,sum,mean,stddev,min,max for a given raster band of a raster or raster coverage. Band 1 is assumed is no band is specified.';



CREATE FUNCTION public.st_summarystats(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true) RETURNS public.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, $3, $4, 1) $_$;




COMMENT ON FUNCTION public.st_summarystats(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true - Returns summary stats consisting of count,sum,mean,stddev,min,max for a given raster band of a raster or raster coverage. Band 1 is assumed is no band is specified.';



CREATE FUNCTION public.st_valuecount(rastertable text, rastercolumn text, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT count integer) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, count FROM _st_valuecount($1, $2, 1, TRUE, $3, $4) $_$;




COMMENT ON FUNCTION public.st_valuecount(rastertable text, rastercolumn text, searchvalues double precision[], roundto double precision, OUT value double precision, OUT count integer) IS 'args: rastertable, rastercolumn, searchvalues, roundto=0, OUT value, OUT count - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';



CREATE FUNCTION public.st_valuecount(rastertable text, rastercolumn text, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, 1, TRUE, ARRAY[$3]::double precision[], $4)).count $_$;




COMMENT ON FUNCTION public.st_valuecount(rastertable text, rastercolumn text, searchvalue double precision, roundto double precision) IS 'args: rastertable, rastercolumn, searchvalue, roundto=0 - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';



CREATE FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT count integer) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, count FROM _st_valuecount($1, $2, $3, TRUE, $4, $5) $_$;




COMMENT ON FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, searchvalues double precision[], roundto double precision, OUT value double precision, OUT count integer) IS 'args: rastertable, rastercolumn, nband, searchvalues, roundto=0, OUT value, OUT count - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';



CREATE FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, TRUE, ARRAY[$4]::double precision[], $5)).count $_$;




COMMENT ON FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, searchvalue double precision, roundto double precision) IS 'args: rastertable, rastercolumn, nband, searchvalue, roundto=0 - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';



CREATE FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, searchvalues double precision[] DEFAULT NULL::double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT count integer) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, count FROM _st_valuecount($1, $2, $3, $4, $5, $6) $_$;




COMMENT ON FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, searchvalues double precision[], roundto double precision, OUT value double precision, OUT count integer) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true, searchvalues=NULL, roundto=0, OUT value, OUT count - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';



CREATE FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, $4, ARRAY[$5]::double precision[], $6)).count $_$;




COMMENT ON FUNCTION public.st_valuecount(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, searchvalue double precision, roundto double precision) IS 'args: rastertable, rastercolumn, nband, exclude_nodata_value, searchvalue, roundto=0 - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';



CREATE FUNCTION public.st_valuepercent(rastertable text, rastercolumn text, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT percent double precision) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, percent FROM _st_valuecount($1, $2, 1, TRUE, $3, $4) $_$;




CREATE FUNCTION public.st_valuepercent(rastertable text, rastercolumn text, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, 1, TRUE, ARRAY[$3]::double precision[], $4)).percent $_$;




CREATE FUNCTION public.st_valuepercent(rastertable text, rastercolumn text, nband integer, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT percent double precision) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, percent FROM _st_valuecount($1, $2, $3, TRUE, $4, $5) $_$;




CREATE FUNCTION public.st_valuepercent(rastertable text, rastercolumn text, nband integer, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, TRUE, ARRAY[$4]::double precision[], $5)).percent $_$;




CREATE FUNCTION public.st_valuepercent(rastertable text, rastercolumn text, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, searchvalues double precision[] DEFAULT NULL::double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT percent double precision) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, percent FROM _st_valuecount($1, $2, $3, $4, $5, $6) $_$;




CREATE FUNCTION public.st_valuepercent(rastertable text, rastercolumn text, nband integer, exclude_nodata_value boolean, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, $4, ARRAY[$5]::double precision[], $6)).percent $_$;




CREATE FUNCTION public.unlockrows(text) RETURNS integer
    LANGUAGE plpgsql STRICT
    AS $_$ 
DECLARE
	ret int;
BEGIN

	IF NOT LongTransactionsEnabled() THEN
		RAISE EXCEPTION 'Long transaction support disabled, use EnableLongTransaction() to enable.';
	END IF;

	EXECUTE 'DELETE FROM authorization_table where authid = ' ||
		quote_literal($1);

	GET DIAGNOSTICS ret = ROW_COUNT;

	RETURN ret;
END;
$_$;




CREATE FUNCTION public.updategeometrysrid(character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT UpdateGeometrySRID('','',$1,$2,$3) into ret;
	RETURN ret;
END;
$_$;




CREATE FUNCTION public.updategeometrysrid(character varying, character varying, character varying, integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $_$
DECLARE
	ret  text;
BEGIN
	SELECT UpdateGeometrySRID('',$1,$2,$3,$4) into ret;
	RETURN ret;
END;
$_$;




CREATE FUNCTION public.updategeometrysrid(catalogn_name character varying, schema_name character varying, table_name character varying, column_name character varying, new_srid_in integer) RETURNS text
    LANGUAGE plpgsql STRICT
    AS $$
DECLARE
	myrec RECORD;
	okay boolean;
	cname varchar;
	real_schema name;
	unknown_srid integer;
	new_srid integer := new_srid_in;

BEGIN


	IF ( schema_name != '' ) THEN
		okay = false;

		FOR myrec IN SELECT nspname FROM pg_namespace WHERE text(nspname) = schema_name LOOP
			okay := true;
		END LOOP;

		IF ( okay <> true ) THEN
			RAISE EXCEPTION 'Invalid schema name';
		ELSE
			real_schema = schema_name;
		END IF;
	ELSE
		SELECT INTO real_schema current_schema()::text;
	END IF;

	okay = false;
	FOR myrec IN SELECT type, coord_dimension FROM geometry_columns WHERE f_table_schema = text(real_schema) and f_table_name = table_name and f_geometry_column = column_name LOOP
		okay := true;
	END LOOP;
	IF (NOT okay) THEN
		RAISE EXCEPTION 'column not found in geometry_columns table';
		RETURN false;
	END IF;

	IF ( new_srid > 0 ) THEN
		IF ( SELECT count(*) = 0 from spatial_ref_sys where srid = new_srid ) THEN
			RAISE EXCEPTION 'invalid SRID: % not found in spatial_ref_sys', new_srid;
			RETURN false;
		END IF;
	ELSE
		unknown_srid := ST_SRID('POINT EMPTY'::geometry);
		IF ( new_srid != unknown_srid ) THEN
			new_srid := unknown_srid;
			RAISE NOTICE 'SRID value % converted to the officially unknown SRID value %', new_srid_in, new_srid;
		END IF;
	END IF;

	IF postgis_constraint_srid(schema_name, table_name, column_name) IS NOT NULL THEN 
        cname = 'enforce_srid_'  || column_name;
    
        EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) ||
            '.' || quote_ident(table_name) ||
            ' DROP constraint ' || quote_ident(cname);
    
        EXECUTE 'UPDATE ' || quote_ident(real_schema) ||
            '.' || quote_ident(table_name) ||
            ' SET ' || quote_ident(column_name) ||
            ' = ST_SetSRID(' || quote_ident(column_name) ||
            ', ' || new_srid::text || ')';
            
        EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) ||
            '.' || quote_ident(table_name) ||
            ' ADD constraint ' || quote_ident(cname) ||
            ' CHECK (st_srid(' || quote_ident(column_name) ||
            ') = ' || new_srid::text || ')';
    ELSE 
        EXECUTE 'ALTER TABLE ' || quote_ident(real_schema) || '.' || quote_ident(table_name) || 
        ' ALTER COLUMN ' || quote_ident(column_name) || ' TYPE  geometry(' || postgis_type_name(myrec.type, myrec.coord_dimension, true) || ', ' || new_srid::text || ') USING ST_SetSRID(' || quote_ident(column_name) || ',' || new_srid::text || ');' ;
    END IF;

	RETURN real_schema || '.' || table_name || '.' || column_name ||' SRID changed to ' || new_srid::text;

END;
$$;




CREATE OPERATOR FAMILY public.btree_geography_ops USING btree;




CREATE OPERATOR FAMILY public.btree_geometry_ops USING btree;




CREATE OPERATOR FAMILY public.gist_geography_ops USING gist;




CREATE OPERATOR FAMILY public.gist_geometry_ops_2d USING gist;




CREATE OPERATOR FAMILY public.gist_geometry_ops_nd USING gist;



SET default_tablespace = '';

SET default_with_oids = false;


CREATE TABLE public.a (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.abc (
    emp_dept integer,
    emp_idno integer,
    rnk2 bigint
);




CREATE TABLE public.actor (
    act_id integer NOT NULL,
    act_fname character(20),
    act_lname character(20),
    act_gender character(1)
);




CREATE TABLE public.actorsbackup2017 (
    act_fname character(20),
    act_lname character(20)
);




CREATE TABLE public.address (
    city character varying(15)
);




CREATE TABLE public.affiliated_with (
    physician integer NOT NULL,
    department integer NOT NULL,
    primaryaffiliation boolean NOT NULL
);




CREATE TABLE public.customer (
    customer_id numeric(5,0) NOT NULL,
    cust_name character varying(30) NOT NULL,
    city character varying(15),
    grade numeric(3,0) DEFAULT 0,
    salesman_id numeric(5,0) NOT NULL
);




CREATE VIEW public.agentview AS
 SELECT customer.customer_id,
    customer.cust_name,
    customer.city,
    customer.grade,
    customer.salesman_id
   FROM public.customer;




CREATE TABLE public.ahmed (
    employee_id numeric(6,0),
    department_id numeric(4,0),
    salary numeric(8,2)
);




CREATE TABLE public.appointment (
    appointmentid integer NOT NULL,
    patient integer NOT NULL,
    prepnurse integer,
    physician integer NOT NULL,
    start_dt_time timestamp without time zone NOT NULL,
    end_dt_time timestamp without time zone NOT NULL,
    examinationroom text NOT NULL
);




CREATE TABLE public.asst_referee_mast (
    ass_ref_id numeric NOT NULL,
    ass_ref_name character varying(40) NOT NULL,
    country_id numeric NOT NULL
);




CREATE TABLE public.bh (
    name character varying(20) NOT NULL,
    emp_id integer NOT NULL,
    process character varying(20)
);




CREATE TABLE public.bitch (
    sum bigint
);




CREATE TABLE public.blah (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.block (
    blockfloor integer NOT NULL,
    blockcode integer NOT NULL
);




CREATE TABLE public.casino (
    pricerange numeric(6,0) NOT NULL,
    events character varying(50),
    location character varying(40) NOT NULL,
    casino character varying(75) NOT NULL
);




CREATE TABLE public.coach_mast (
    coach_id numeric NOT NULL,
    coach_name character varying(40) NOT NULL
);




CREATE TABLE public.col1 (
    "?column?" integer
);




CREATE TABLE public.company_mast (
    com_id integer NOT NULL,
    com_name character varying(20) NOT NULL
);




CREATE TABLE public.countries (
    country_id character varying(2),
    country_name character varying(40),
    region_id numeric(10,0)
);




CREATE TABLE public.customer_backup (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.customer_id (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.customer_id_123 (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);




CREATE VIEW public.customergradelevels AS
 SELECT DISTINCT customer.grade,
    count(*) AS count
   FROM public.customer
  GROUP BY customer.grade;




CREATE VIEW public.customergradelevels2 AS
 SELECT DISTINCT customer.grade,
    count(*) AS count
   FROM public.customer
  WHERE (customer.grade IS NOT NULL)
  GROUP BY customer.grade;




CREATE TABLE public.department (
    departmentid integer NOT NULL,
    name text NOT NULL,
    head integer NOT NULL
);




CREATE TABLE public.department_detail (
    first_name character varying(20),
    last_name character varying(25),
    department_id numeric(4,0),
    department_name character varying(30)
);




CREATE TABLE public.departments (
    department_id numeric(4,0) NOT NULL,
    department_name character varying(30) NOT NULL,
    manager_id numeric(6,0) DEFAULT NULL::numeric,
    location_id numeric(4,0) DEFAULT NULL::numeric
);




CREATE TABLE public.director (
    dir_id integer NOT NULL,
    dir_fname character(20),
    dir_lname character(20)
);




CREATE TABLE public.duplicate (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.elephants (
    id integer NOT NULL,
    name text,
    date date DEFAULT now()
);




CREATE TABLE public.emp (
    ename character varying(13),
    salary numeric(6,0),
    eid numeric(3,0) NOT NULL
);




CREATE TABLE public.emp_department (
    dpt_code integer NOT NULL,
    dpt_name character(15) NOT NULL,
    dpt_allotment integer NOT NULL
);




CREATE TABLE public.emp_details (
    emp_idno integer NOT NULL,
    emp_fname character(15) NOT NULL,
    emp_lname character(15) NOT NULL,
    emp_dept integer NOT NULL
);




CREATE TABLE public.employee (
    empno integer,
    ename character varying(20)
);




CREATE TABLE public.employees (
    employee_id numeric(6,0) DEFAULT (0)::numeric NOT NULL,
    first_name character varying(20) DEFAULT NULL::character varying,
    last_name character varying(25) NOT NULL,
    email character varying(25) NOT NULL,
    phone_number character varying(20) DEFAULT NULL::character varying,
    hire_date date NOT NULL,
    job_id character varying(10) NOT NULL,
    salary numeric(8,2) DEFAULT NULL::numeric,
    commission_pct numeric(2,2) DEFAULT NULL::numeric,
    manager_id numeric(6,0) DEFAULT NULL::numeric,
    department_id numeric(4,0) DEFAULT NULL::numeric
);




CREATE TABLE public.ff (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2),
    year integer,
    subject character(25),
    winner character(45),
    country character(25),
    category character(25)
);




CREATE TABLE public.fg (
    customer_id numeric(5,0),
    avg numeric
);




CREATE TABLE public.game_scores (
    id integer NOT NULL,
    name text,
    score integer
);




CREATE TABLE public.genres (
    gen_id integer NOT NULL,
    gen_title character(20)
);




CREATE TABLE public.goal_details (
    goal_id numeric NOT NULL,
    match_no numeric NOT NULL,
    player_id numeric NOT NULL,
    team_id numeric NOT NULL,
    goal_time numeric NOT NULL,
    goal_type character(1) NOT NULL,
    play_stage character(1) NOT NULL,
    goal_schedule character(2) NOT NULL,
    goal_half numeric
);




CREATE TABLE public.grade (
    city character varying(15)
);




CREATE VIEW public.grade_customer AS
 SELECT customer.grade,
    count(customer.customer_id) AS count
   FROM public.customer
  GROUP BY customer.grade;




CREATE VIEW public.grade_customer1 AS
 SELECT customer.grade,
    count(*) AS number
   FROM public.customer
  GROUP BY customer.grade;




CREATE TABLE public.grades (
    id integer NOT NULL,
    grade_1 integer,
    grade_2 integer,
    grade_3 integer
);




CREATE TABLE public.hello (
    number_one integer,
    number_two integer,
    number_three integer
);




CREATE TABLE public.hello1_1122 (
    abc integer
);




CREATE TABLE public.hello1_12 (
    abc integer
);




CREATE TABLE public.hello_12 (
    abc integer
);




CREATE TABLE public.item_mast (
    pro_id integer NOT NULL,
    pro_name character varying(25) NOT NULL,
    pro_price numeric(8,2) NOT NULL,
    pro_com integer NOT NULL
);




CREATE TABLE public.job_grades (
    grade_level character varying(20) NOT NULL,
    lowest_sal numeric(5,0) NOT NULL,
    highest_sal numeric(5,0) NOT NULL
);




CREATE TABLE public.job_history (
    employee_id numeric(6,0) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    job_id character varying(10) NOT NULL,
    department_id numeric(4,0) DEFAULT NULL::numeric
);




CREATE TABLE public.jobs (
    job_id character varying(10) DEFAULT ''::character varying NOT NULL,
    job_title character varying(35) NOT NULL,
    min_salary numeric(6,0) DEFAULT NULL::numeric,
    max_salary numeric(6,0) DEFAULT NULL::numeric
);




CREATE TABLE public.kk (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.kkk (
    name character varying(30)
);




CREATE TABLE public.locations (
    location_id numeric(4,0) DEFAULT (0)::numeric NOT NULL,
    street_address character varying(40) DEFAULT NULL::character varying,
    postal_code character varying(12) DEFAULT NULL::character varying,
    city character varying(30) NOT NULL,
    state_province character varying(25) DEFAULT NULL::character varying,
    country_id character varying(2) DEFAULT NULL::character varying
);




CREATE TABLE public.londoncustomers (
    cust_name character varying(30),
    city character varying(15)
);




CREATE TABLE public.manufacturers (
    code integer NOT NULL,
    name character varying(255) NOT NULL
);




CREATE TABLE public.match_captain (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_captain numeric NOT NULL
);




CREATE TABLE public.match_details (
    match_no numeric NOT NULL,
    play_stage character(1) NOT NULL,
    team_id numeric NOT NULL,
    win_lose character(1) NOT NULL,
    decided_by character(1) NOT NULL,
    goal_score numeric NOT NULL,
    penalty_score numeric,
    ass_ref numeric NOT NULL,
    player_gk numeric NOT NULL
);




CREATE TABLE public.match_mast (
    match_no numeric NOT NULL,
    play_stage character(1) NOT NULL,
    play_date date NOT NULL,
    results character(5) NOT NULL,
    decided_by character(1) NOT NULL,
    goal_score character(5) NOT NULL,
    venue_id numeric NOT NULL,
    referee_id numeric NOT NULL,
    audence numeric NOT NULL,
    plr_of_match numeric NOT NULL,
    stop1_sec numeric NOT NULL,
    stop2_sec numeric NOT NULL
);




CREATE TABLE public.maxim00 (
    num integer,
    name character varying(10)
);




CREATE TABLE public.maximum (
    num integer,
    name character varying(10)
);




CREATE TABLE public.maximum00 (
    num integer,
    name character varying(10)
);




CREATE TABLE public.maximum899 (
    num integer,
    name character varying(10)
);




CREATE TABLE public.medication (
    code integer NOT NULL,
    name text NOT NULL,
    brand text NOT NULL,
    description text NOT NULL
);




CREATE TABLE public.movie (
    mov_id integer NOT NULL,
    mov_title character(50),
    mov_year integer,
    mov_time integer,
    mov_lang character(15),
    mov_dt_rel date,
    mov_rel_country character(5)
);




CREATE TABLE public.movie_cast (
    act_id integer NOT NULL,
    mov_id integer NOT NULL,
    role character(30)
);




CREATE TABLE public.movie_direction (
    dir_id integer NOT NULL,
    mov_id integer NOT NULL
);




CREATE TABLE public.movie_genres (
    mov_id integer NOT NULL,
    gen_id integer NOT NULL
);




CREATE TABLE public.my (
    customer_id numeric(5,0),
    avg numeric
);




CREATE TABLE public.mytemptable (
    customer_id numeric(5,0),
    avg numeric
);




CREATE TABLE public.mytest (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);




CREATE TABLE public.mytest1 (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);




CREATE TABLE public.salesman (
    salesman_id numeric(5,0) NOT NULL,
    name character varying(30) NOT NULL,
    city character varying(15),
    commission numeric(5,2)
);




CREATE VIEW public.myworkstuff AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman;




CREATE VIEW public.myworkstuffs AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman
  WHERE ((salesman.city)::text = 'New York'::text);




CREATE TABLE public.new (
    city character varying(15)
);




CREATE TABLE public.new123 (
    "Customer" character varying(30),
    city character varying(15),
    "Salesman" character varying(30),
    commission numeric(5,2)
);




CREATE TABLE public.new_table (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.newsalesman (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.newtab (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.newtable (
    roomnumber integer,
    roomtype character varying(30),
    blockfloor integer,
    blockcode integer,
    unavailable boolean
);




CREATE VIEW public.newyorksalesman AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM public.salesman
  WHERE ((salesman.city)::text = 'new york'::text);




CREATE VIEW public.newyorksalesman2 AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM public.salesman
  WHERE ((salesman.city)::text = 'rome'::text);




CREATE VIEW public.newyorksalesman3 AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM public.salesman
  WHERE ((salesman.city)::text = 'Rome'::text);




CREATE VIEW public.newyorkstaff AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM public.salesman
  WHERE ((salesman.city)::text = 'New York'::text);




CREATE TABLE public.nobel_win (
    year integer,
    subject character(25),
    winner character(45),
    country character(25),
    category character(25)
);




CREATE TABLE public.orders (
    ord_no numeric(5,0) NOT NULL,
    purch_amt numeric(8,2) DEFAULT 0,
    ord_date date,
    customer_id numeric(5,0) NOT NULL,
    salesman_id numeric(5,0) NOT NULL
);




CREATE VIEW public.norders AS
 SELECT salesman.name,
    avg(orders.purch_amt) AS avg,
    sum(orders.purch_amt) AS sum
   FROM public.salesman,
    public.orders
  WHERE (salesman.salesman_id = orders.salesman_id)
  GROUP BY salesman.name;




CREATE TABLE public.nros (
    uno integer,
    dos integer
);




CREATE TABLE public.nuevo (
    city character varying(15)
);




CREATE TABLE public.numbers (
    one integer,
    two integer,
    three integer
);




CREATE TABLE public.numeri (
    id integer,
    data date,
    decimali real
);




CREATE TABLE public.numeros (
    uno integer,
    dos integer
);




CREATE TABLE public.nurse (
    employeeid integer NOT NULL,
    name text NOT NULL,
    "position" text NOT NULL,
    registered boolean NOT NULL,
    ssn integer NOT NULL
);




CREATE VIEW public.odr AS
 SELECT orders.ord_no
   FROM public.orders
  WHERE ((orders.ord_no >= (70002)::numeric) AND (orders.ord_no <= (70008)::numeric));




CREATE TABLE public.oi (
    customer_id numeric(5,0),
    avg numeric
);




CREATE TABLE public.on_call (
    nurse integer NOT NULL,
    blockfloor integer NOT NULL,
    blockcode integer NOT NULL,
    oncallstart timestamp without time zone NOT NULL,
    oncallend timestamp without time zone NOT NULL
);




CREATE VIEW public.ordersview AS
 SELECT orders.ord_no,
    orders.purch_amt
   FROM public.orders
  WHERE (orders.purch_amt < (500)::numeric);




CREATE TABLE public.orozco (
    city character varying(15)
);




CREATE TABLE public.partest1 (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);




CREATE TABLE public.participant (
    participant_id integer NOT NULL,
    part_name character varying(20) NOT NULL
);




CREATE TABLE public.participants (
    participant_id integer NOT NULL,
    part_name character varying(20) NOT NULL
);




CREATE TABLE public.patient (
    ssn integer NOT NULL,
    name text NOT NULL,
    address text NOT NULL,
    phone text NOT NULL,
    insuranceid integer NOT NULL,
    pcp integer NOT NULL
);




CREATE TABLE public.penalty_gk (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_gk numeric NOT NULL
);




CREATE TABLE public.penalty_shootout (
    kick_id numeric NOT NULL,
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_id numeric NOT NULL,
    score_goal character(1) NOT NULL,
    kick_no numeric NOT NULL
);




CREATE TABLE public.persons (
    personid integer,
    lastname character varying(255),
    firstname character varying(255),
    address character varying(255),
    city character varying(255)
);




CREATE TABLE public.physician (
    employeeid integer NOT NULL,
    name text NOT NULL,
    "position" text NOT NULL,
    ssn integer NOT NULL
);




CREATE TABLE public.player_booked (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_id numeric NOT NULL,
    booking_time numeric NOT NULL,
    sent_off character(1) DEFAULT NULL::bpchar,
    play_schedule character(2) NOT NULL,
    play_half numeric NOT NULL
);




CREATE TABLE public.player_in_out (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_id numeric NOT NULL,
    in_out character(1) NOT NULL,
    time_in_out numeric NOT NULL,
    play_schedule character(2) NOT NULL,
    play_half numeric NOT NULL
);




CREATE TABLE public.player_mast (
    player_id numeric NOT NULL,
    team_id numeric NOT NULL,
    jersey_no numeric NOT NULL,
    player_name character varying(40) NOT NULL,
    posi_to_play character(2) NOT NULL,
    dt_of_bir date,
    age numeric,
    playing_club character varying(40)
);




CREATE TABLE public.playing_position (
    position_id character(2) NOT NULL,
    position_desc character varying(15) NOT NULL
);




CREATE TABLE public.prescribes (
    physician integer NOT NULL,
    patient integer NOT NULL,
    medication integer NOT NULL,
    date timestamp without time zone NOT NULL,
    appointment integer,
    dose text NOT NULL
);




CREATE TABLE public.procedure (
    code integer NOT NULL,
    name text NOT NULL,
    cost real NOT NULL
);




CREATE VIEW public.raster_overviews AS
 SELECT current_database() AS o_table_catalog,
    n.nspname AS o_table_schema,
    c.relname AS o_table_name,
    a.attname AS o_raster_column,
    current_database() AS r_table_catalog,
    (split_part(split_part(s.consrc, '''::name'::text, 1), ''''::text, 2))::name AS r_table_schema,
    (split_part(split_part(s.consrc, '''::name'::text, 2), ''''::text, 2))::name AS r_table_name,
    (split_part(split_part(s.consrc, '''::name'::text, 3), ''''::text, 2))::name AS r_raster_column,
    (btrim(split_part(s.consrc, ','::text, 2)))::integer AS overview_factor
   FROM pg_class c,
    pg_attribute a,
    pg_type t,
    pg_namespace n,
    pg_constraint s
  WHERE ((((((((((t.typname = 'raster'::name) AND (a.attisdropped = false)) AND (a.atttypid = t.oid)) AND (a.attrelid = c.oid)) AND (c.relnamespace = n.oid)) AND ((c.relkind = 'r'::"char") OR (c.relkind = 'v'::"char"))) AND (s.connamespace = n.oid)) AND (s.conrelid = c.oid)) AND (s.consrc ~~ '%_overview_constraint(%'::text)) AND (NOT pg_is_other_temp_schema(c.relnamespace)));




CREATE TABLE public.rating (
    mov_id integer NOT NULL,
    rev_id integer NOT NULL,
    rev_stars numeric(4,2),
    num_o_ratings integer
);




CREATE TABLE public.referee_mast (
    referee_id numeric NOT NULL,
    referee_name character varying(40) NOT NULL,
    country_id numeric NOT NULL
);




CREATE TABLE public.regions (
    region_id numeric(10,0) NOT NULL,
    region_name character(25)
);




CREATE TABLE public.related (
    city character varying(15)
);




CREATE TABLE public.reviewer (
    rev_id integer NOT NULL,
    rev_name character(30)
);




CREATE VIEW public.rightjoins AS
 SELECT t1.customer_id,
    t1.grade,
    t2.salesman_id
   FROM (public.customer t1
     RIGHT JOIN public.orders t2 ON ((t1.customer_id = t2.customer_id)))
  WHERE (t1.grade IS NOT NULL)
  ORDER BY t1.customer_id;




CREATE TABLE public.room (
    roomnumber integer NOT NULL,
    roomtype character varying(30) NOT NULL,
    blockfloor integer NOT NULL,
    blockcode integer NOT NULL,
    unavailable boolean NOT NULL
);




CREATE VIEW public.salesdetail AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.name
   FROM public.salesman;




CREATE VIEW public.salesman_detail AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.name
   FROM public.salesman;




CREATE TABLE public.salesman_do1304 (
    salesman_id character varying(20),
    name character varying(40),
    city character varying(30),
    commission character varying(10)
);




CREATE VIEW public.salesman_example AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman;




CREATE VIEW public.salesman_ny AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.commission
   FROM public.salesman
  WHERE (((salesman.city)::text = 'New York'::text) AND (salesman.commission > 0.13));




CREATE VIEW public.salesmandetail AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.name
   FROM public.salesman;




CREATE VIEW public.salesown AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman;




CREATE TABLE public.sample_table (
    salesman_id character(4),
    name character varying(20),
    city character varying(20),
    commission character(10)
);




CREATE TABLE public.scores (
    id integer NOT NULL,
    score integer
);




CREATE TABLE public.soccer_city (
    city_id numeric NOT NULL,
    city character varying(25) NOT NULL,
    country_id numeric NOT NULL
);




CREATE TABLE public.soccer_country (
    country_id numeric NOT NULL,
    country_abbr character varying(4) NOT NULL,
    country_name character varying(40) NOT NULL
);




CREATE TABLE public.soccer_team (
    team_id numeric NOT NULL,
    team_group character(1) NOT NULL,
    match_played numeric NOT NULL,
    won numeric NOT NULL,
    draw numeric NOT NULL,
    lost numeric NOT NULL,
    goal_for numeric NOT NULL,
    goal_agnst numeric NOT NULL,
    goal_diff numeric NOT NULL,
    points numeric NOT NULL,
    group_position numeric NOT NULL
);




CREATE TABLE public.soccer_venue (
    venue_id numeric NOT NULL,
    venue_name character varying(30) NOT NULL,
    city_id numeric NOT NULL,
    aud_capacity numeric NOT NULL
);




CREATE TABLE public.statements (
    name text
);




CREATE TABLE public.stay (
    stayid integer NOT NULL,
    patient integer NOT NULL,
    room integer NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone NOT NULL
);




CREATE TABLE public.string (
    text text
);




CREATE TABLE public.student (
    roll integer,
    name character varying
);




CREATE TABLE public.student1 (
    roll integer,
    name character varying
);




CREATE TABLE public.sybba (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);




CREATE TABLE public.table1 (
    "Customer Name" character varying(30),
    city character varying(15),
    "Salesman" character varying(30),
    commission numeric(5,2)
);




CREATE TABLE public.team_coaches (
    team_id numeric NOT NULL,
    coach_id numeric NOT NULL
);




CREATE TABLE public.temp (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.tempa (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.tempcustomer (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.temphi (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.tempp (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.tempp11 (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);




CREATE TABLE public.tempsalesman (
    salesman_id numeric(5,0),
    name character varying(30),
    city character varying(15),
    commission numeric(5,2)
);




CREATE TABLE public.test (
    x integer
);




CREATE TABLE public.teste (
    salesman_id numeric(5,0),
    count bigint
);




CREATE TABLE public.testtable (
    col1 character varying(30) NOT NULL
);




COMMENT ON TABLE public.testtable IS 'a temporary table for some queries ';



CREATE VIEW public.testtesing AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman
  WHERE ((salesman.city)::text = 'New York'::text);




CREATE VIEW public.testtest AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman;




CREATE TABLE public.trained_in (
    physician integer NOT NULL,
    treatment integer NOT NULL,
    certificationdate date NOT NULL,
    certificationexpires date NOT NULL
);




CREATE TABLE public.trenta (
    numeri integer
);




CREATE TABLE public.tt (
    name integer
);




CREATE TABLE public.undergoes (
    patient integer NOT NULL,
    procedure integer NOT NULL,
    stay integer NOT NULL,
    date timestamp without time zone NOT NULL,
    physician integer NOT NULL,
    assistingnurse integer
);




CREATE VIEW public.v1 AS
 SELECT o.ord_no,
    c.cust_name,
    s.name
   FROM ((public.orders o
     JOIN public.customer c ON ((o.customer_id = c.customer_id)))
     JOIN public.salesman s ON ((o.salesman_id = s.salesman_id)));




CREATE VIEW public.view AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM public.salesman;




CREATE TABLE public.vowl (
    cust_name character varying(30),
    "substring" text
);




CREATE TABLE public.zebras (
    id integer NOT NULL,
    score integer,
    date date DEFAULT now()
);




CREATE TABLE public.zz (
    customer_id numeric(5,0),
    avg numeric
);





ALTER TABLE ONLY public.actor
    ADD CONSTRAINT actor_pkey PRIMARY KEY (act_id);



ALTER TABLE ONLY public.affiliated_with
    ADD CONSTRAINT affiliated_with_pkey PRIMARY KEY (physician, department);



ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT appointment_pkey PRIMARY KEY (appointmentid);



ALTER TABLE ONLY public.asst_referee_mast
    ADD CONSTRAINT asst_referee_mast_pkey PRIMARY KEY (ass_ref_id);



ALTER TABLE ONLY public.block
    ADD CONSTRAINT block_pkey PRIMARY KEY (blockfloor, blockcode);



ALTER TABLE ONLY public.casino
    ADD CONSTRAINT casino_pkey PRIMARY KEY (casino);



ALTER TABLE ONLY public.coach_mast
    ADD CONSTRAINT coach_mast_pkey PRIMARY KEY (coach_id);



ALTER TABLE ONLY public.company_mast
    ADD CONSTRAINT company_mast_pkey PRIMARY KEY (com_id);



ALTER TABLE ONLY public.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);



ALTER TABLE ONLY public.department
    ADD CONSTRAINT department_pkey PRIMARY KEY (departmentid);



ALTER TABLE ONLY public.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (department_id);



ALTER TABLE ONLY public.director
    ADD CONSTRAINT director_pkey PRIMARY KEY (dir_id);



ALTER TABLE ONLY public.elephants
    ADD CONSTRAINT elephants_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.emp_department
    ADD CONSTRAINT emp_department_pkey PRIMARY KEY (dpt_code);



ALTER TABLE ONLY public.emp_details
    ADD CONSTRAINT emp_details_pkey PRIMARY KEY (emp_idno);



ALTER TABLE ONLY public.emp
    ADD CONSTRAINT emp_pk PRIMARY KEY (eid);



ALTER TABLE ONLY public.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (employee_id);



ALTER TABLE ONLY public.game_scores
    ADD CONSTRAINT game_scores_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.genres
    ADD CONSTRAINT genres_pkey PRIMARY KEY (gen_id);



ALTER TABLE ONLY public.goal_details
    ADD CONSTRAINT goal_details_pkey PRIMARY KEY (goal_id);



ALTER TABLE ONLY public.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.item_mast
    ADD CONSTRAINT item_mast_pkey PRIMARY KEY (pro_id);



ALTER TABLE ONLY public.job_grades
    ADD CONSTRAINT job_grades_pkey PRIMARY KEY (grade_level);



ALTER TABLE ONLY public.job_history
    ADD CONSTRAINT job_history_pkey PRIMARY KEY (employee_id, start_date);



ALTER TABLE ONLY public.jobs
    ADD CONSTRAINT jobs_pkey PRIMARY KEY (job_id);



ALTER TABLE ONLY public.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (location_id);



ALTER TABLE ONLY public.manufacturers
    ADD CONSTRAINT manufacturers_pkey PRIMARY KEY (code);



ALTER TABLE ONLY public.match_mast
    ADD CONSTRAINT match_mast_pkey PRIMARY KEY (match_no);



ALTER TABLE ONLY public.medication
    ADD CONSTRAINT medication_pkey PRIMARY KEY (code);



ALTER TABLE ONLY public.movie
    ADD CONSTRAINT movie_pkey PRIMARY KEY (mov_id);



ALTER TABLE ONLY public.mytest1
    ADD CONSTRAINT mytest1_ord_num_key UNIQUE (ord_num);



ALTER TABLE ONLY public.mytest
    ADD CONSTRAINT mytest_ord_num_key UNIQUE (ord_num);



ALTER TABLE ONLY public.nurse
    ADD CONSTRAINT nurse_pkey PRIMARY KEY (employeeid);



ALTER TABLE ONLY public.on_call
    ADD CONSTRAINT on_call_pkey PRIMARY KEY (nurse, blockfloor, blockcode, oncallstart, oncallend);



ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (ord_no);



ALTER TABLE ONLY public.partest1
    ADD CONSTRAINT partest1_ord_num_key UNIQUE (ord_num);



ALTER TABLE ONLY public.patient
    ADD CONSTRAINT patient_pkey PRIMARY KEY (ssn);



ALTER TABLE ONLY public.penalty_shootout
    ADD CONSTRAINT penalty_shootout_pkey PRIMARY KEY (kick_id);



ALTER TABLE ONLY public.physician
    ADD CONSTRAINT physician_pkey PRIMARY KEY (employeeid);



ALTER TABLE ONLY public.player_mast
    ADD CONSTRAINT player_mast_pkey PRIMARY KEY (player_id);



ALTER TABLE ONLY public.playing_position
    ADD CONSTRAINT playing_position_pkey PRIMARY KEY (position_id);



ALTER TABLE ONLY public.prescribes
    ADD CONSTRAINT prescribes_pkey PRIMARY KEY (physician, patient, medication, date);



ALTER TABLE ONLY public.procedure
    ADD CONSTRAINT procedure_pkey PRIMARY KEY (code);



ALTER TABLE ONLY public.referee_mast
    ADD CONSTRAINT referee_mast_pkey PRIMARY KEY (referee_id);



ALTER TABLE ONLY public.regions
    ADD CONSTRAINT regions_pkey PRIMARY KEY (region_id);



ALTER TABLE ONLY public.reviewer
    ADD CONSTRAINT reviewer_pkey PRIMARY KEY (rev_id);



ALTER TABLE ONLY public.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (roomnumber);



ALTER TABLE ONLY public.salesman
    ADD CONSTRAINT salesman_pkey PRIMARY KEY (salesman_id);



ALTER TABLE ONLY public.scores
    ADD CONSTRAINT scores_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.soccer_city
    ADD CONSTRAINT soccer_city_pkey PRIMARY KEY (city_id);



ALTER TABLE ONLY public.soccer_country
    ADD CONSTRAINT soccer_country_pkey PRIMARY KEY (country_id);



ALTER TABLE ONLY public.soccer_venue
    ADD CONSTRAINT soccer_venue_pkey PRIMARY KEY (venue_id);



ALTER TABLE ONLY public.stay
    ADD CONSTRAINT stay_pkey PRIMARY KEY (stayid);



ALTER TABLE ONLY public.sybba
    ADD CONSTRAINT sybba_ord_num_key UNIQUE (ord_num);



ALTER TABLE ONLY public.trained_in
    ADD CONSTRAINT trained_in_pkey PRIMARY KEY (physician, treatment);



ALTER TABLE ONLY public.undergoes
    ADD CONSTRAINT undergoes_pkey PRIMARY KEY (patient, procedure, stay, date);



ALTER TABLE ONLY public.zebras
    ADD CONSTRAINT zebras_pkey PRIMARY KEY (id);



ALTER TABLE ONLY public.match_details
    ADD CONSTRAINT ass_ref_fkey FOREIGN KEY (ass_ref) REFERENCES public.asst_referee_mast(ass_ref_id);



ALTER TABLE ONLY public.soccer_venue
    ADD CONSTRAINT city_id_fkey FOREIGN KEY (city_id) REFERENCES public.soccer_city(city_id);



ALTER TABLE ONLY public.team_coaches
    ADD CONSTRAINT coach_id_fkey FOREIGN KEY (coach_id) REFERENCES public.coach_mast(coach_id);



ALTER TABLE ONLY public.soccer_city
    ADD CONSTRAINT country_id_fkey FOREIGN KEY (country_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.referee_mast
    ADD CONSTRAINT country_id_fkey FOREIGN KEY (country_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.asst_referee_mast
    ADD CONSTRAINT country_id_fkey FOREIGN KEY (country_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.orders
    ADD CONSTRAINT customer_id_fk FOREIGN KEY (customer_id) REFERENCES public.customer(customer_id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY public.emp_details
    ADD CONSTRAINT emp_details_emp_dept_fkey FOREIGN KEY (emp_dept) REFERENCES public.emp_department(dpt_code);



ALTER TABLE ONLY public.prescribes
    ADD CONSTRAINT fk_appointment_appointmentid FOREIGN KEY (appointment) REFERENCES public.appointment(appointmentid);



ALTER TABLE ONLY public.affiliated_with
    ADD CONSTRAINT fk_department_departmentid FOREIGN KEY (department) REFERENCES public.department(departmentid);



ALTER TABLE ONLY public.prescribes
    ADD CONSTRAINT fk_medication_code FOREIGN KEY (medication) REFERENCES public.medication(code);



ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fk_nurse_employeeid FOREIGN KEY (prepnurse) REFERENCES public.nurse(employeeid);



ALTER TABLE ONLY public.undergoes
    ADD CONSTRAINT fk_nurse_employeeid FOREIGN KEY (assistingnurse) REFERENCES public.nurse(employeeid);



ALTER TABLE ONLY public.on_call
    ADD CONSTRAINT fk_oncall_block_floor FOREIGN KEY (blockfloor, blockcode) REFERENCES public.block(blockfloor, blockcode);



ALTER TABLE ONLY public.on_call
    ADD CONSTRAINT fk_oncall_nurse_employeeid FOREIGN KEY (nurse) REFERENCES public.nurse(employeeid);



ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES public.patient(ssn);



ALTER TABLE ONLY public.prescribes
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES public.patient(ssn);



ALTER TABLE ONLY public.stay
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES public.patient(ssn);



ALTER TABLE ONLY public.undergoes
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES public.patient(ssn);



ALTER TABLE ONLY public.department
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (head) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.affiliated_with
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.trained_in
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.patient
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (pcp) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.appointment
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.prescribes
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.undergoes
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES public.physician(employeeid);



ALTER TABLE ONLY public.trained_in
    ADD CONSTRAINT fk_procedure_code FOREIGN KEY (treatment) REFERENCES public.procedure(code);



ALTER TABLE ONLY public.undergoes
    ADD CONSTRAINT fk_procedure_code FOREIGN KEY (procedure) REFERENCES public.procedure(code);



ALTER TABLE ONLY public.room
    ADD CONSTRAINT fk_room_block_pk FOREIGN KEY (blockfloor, blockcode) REFERENCES public.block(blockfloor, blockcode);



ALTER TABLE ONLY public.stay
    ADD CONSTRAINT fk_room_number FOREIGN KEY (room) REFERENCES public.room(roomnumber);



ALTER TABLE ONLY public.undergoes
    ADD CONSTRAINT fk_stay_stayid FOREIGN KEY (stay) REFERENCES public.stay(stayid);



ALTER TABLE ONLY public.item_mast
    ADD CONSTRAINT item_mast_pro_com_fkey FOREIGN KEY (pro_com) REFERENCES public.company_mast(com_id);



ALTER TABLE ONLY public.match_details
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.player_booked
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.player_in_out
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.penalty_shootout
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.goal_details
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.match_captain
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.penalty_gk
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES public.match_mast(match_no);



ALTER TABLE ONLY public.movie_cast
    ADD CONSTRAINT movie_cast_act_id_fkey FOREIGN KEY (act_id) REFERENCES public.actor(act_id);



ALTER TABLE ONLY public.movie_cast
    ADD CONSTRAINT movie_cast_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES public.movie(mov_id);



ALTER TABLE ONLY public.movie_direction
    ADD CONSTRAINT movie_direction_dir_id_fkey FOREIGN KEY (dir_id) REFERENCES public.director(dir_id);



ALTER TABLE ONLY public.movie_direction
    ADD CONSTRAINT movie_direction_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES public.movie(mov_id);



ALTER TABLE ONLY public.movie_genres
    ADD CONSTRAINT movie_genres_gen_id_fkey FOREIGN KEY (gen_id) REFERENCES public.genres(gen_id);



ALTER TABLE ONLY public.movie_genres
    ADD CONSTRAINT movie_genres_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES public.movie(mov_id);



ALTER TABLE ONLY public.match_captain
    ADD CONSTRAINT player_captain_fkey FOREIGN KEY (player_captain) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.match_details
    ADD CONSTRAINT player_gk_fkey FOREIGN KEY (player_gk) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.penalty_gk
    ADD CONSTRAINT player_gk_fkey FOREIGN KEY (player_gk) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.player_booked
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.player_in_out
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.penalty_shootout
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.goal_details
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.match_mast
    ADD CONSTRAINT plr_of_match_fkey FOREIGN KEY (plr_of_match) REFERENCES public.player_mast(player_id);



ALTER TABLE ONLY public.player_mast
    ADD CONSTRAINT posi_to_play_fkey FOREIGN KEY (posi_to_play) REFERENCES public.playing_position(position_id);



ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES public.movie(mov_id);



ALTER TABLE ONLY public.rating
    ADD CONSTRAINT rating_rev_id_fkey FOREIGN KEY (rev_id) REFERENCES public.reviewer(rev_id);



ALTER TABLE ONLY public.match_mast
    ADD CONSTRAINT referee_id_fkey FOREIGN KEY (referee_id) REFERENCES public.referee_mast(referee_id);



ALTER TABLE ONLY public.customer
    ADD CONSTRAINT salesman_id_fk FOREIGN KEY (salesman_id) REFERENCES public.salesman(salesman_id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY public.orders
    ADD CONSTRAINT salesman_id_fk2 FOREIGN KEY (salesman_id) REFERENCES public.salesman(salesman_id) ON UPDATE CASCADE ON DELETE CASCADE;



ALTER TABLE ONLY public.team_coaches
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.player_mast
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.soccer_team
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.match_details
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.player_booked
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.player_in_out
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.penalty_shootout
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.goal_details
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.match_captain
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.penalty_gk
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES public.soccer_country(country_id);



ALTER TABLE ONLY public.match_mast
    ADD CONSTRAINT venue_id_fkey FOREIGN KEY (venue_id) REFERENCES public.soccer_venue(venue_id);

