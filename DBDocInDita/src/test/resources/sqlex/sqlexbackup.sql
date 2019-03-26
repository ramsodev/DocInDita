create schema sqlex;


CREATE FUNCTION sqlex.overview_constraint_info(ovschema name, ovtable name, ovcolumn name, OUT refschema name, OUT reftable name, OUT refcolumn name, OUT factor integer) RETURNS record
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
CREATE FUNCTION sqlex.raster_constraint_info_alignment(rastschema name, rasttable name, rastcolumn name) RETURNS boolean
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
CREATE FUNCTION sqlex.raster_constraint_info_blocksize(rastschema name, rasttable name, rastcolumn name, axis CLOB) RETURNS integer
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
CREATE FUNCTION sqlex.raster_constraint_info_nodata_values(rastschema name, rasttable name, rastcolumn name) RETURNS double precision[]
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
CREATE FUNCTION sqlex.raster_constraint_info_num_bands(rastschema name, rasttable name, rastcolumn name) RETURNS integer
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
CREATE FUNCTION sqlex.raster_constraint_info_out_db(rastschema name, rasttable name, rastcolumn name) RETURNS boolean[]
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
CREATE FUNCTION sqlex.raster_constraint_info_pixel_types(rastschema name, rasttable name, rastcolumn name) RETURNS CLOB[]
    LANGUAGE sql STABLE STRICT
    AS $_$
	SELECT
		trim(both '''' from split_part(replace(replace(split_part(s.consrc, ' = ', 2), ')', ''), '(', ''), '::', 1))::CLOB[]
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
CREATE FUNCTION sqlex.raster_constraint_info_scale(rastschema name, rasttable name, rastcolumn name, axis character) RETURNS double precision
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
CREATE FUNCTION sqlex.raster_constraint_info_srid(rastschema name, rasttable name, rastcolumn name) RETURNS integer
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
CREATE FUNCTION sqlex.addoverviewconstraints(ovtable name, ovcolumn name, reftable name, refcolumn name, ovfactor integer) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT AddOverviewConstraints('', $1, $2, '', $3, $4, $5) $_$;
CREATE FUNCTION sqlex.addrasterconstraints(rasttable name, rastcolumn name, VARIADIC constraints CLOB[]) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT AddRasterConstraints('', $1, $2, VARIADIC $3) $_$;
COMMENT ON FUNCTION sqlex.addrasterconstraints(rasttable name, rastcolumn name, VARIADIC constraints CLOB[]) IS 'args: rasttable, rastcolumn, VARIADIC constraints - Adds raster constraints to a loaded raster table for a specific column that constrains spatial ref, scaling, blocksize, alignment, bands, band type and a flag to denote if raster column is regularly blocked. The table must be loaded with data for the constraints to be inferred. Returns true of the constraint setting was accomplished and if issues a notice.';
CREATE FUNCTION sqlex.addrasterconstraints(rasttable name, rastcolumn name, srid boolean DEFAULT true, scale_x boolean DEFAULT true, scale_y boolean DEFAULT true, blocksize_x boolean DEFAULT true, blocksize_y boolean DEFAULT true, same_alignment boolean DEFAULT true, regular_blocking boolean DEFAULT false, num_bands boolean DEFAULT true, pixel_types boolean DEFAULT true, nodata_values boolean DEFAULT true, out_db boolean DEFAULT true, extent boolean DEFAULT true) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT AddRasterConstraints('', $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14) $_$;
COMMENT ON FUNCTION sqlex.addrasterconstraints(rasttable name, rastcolumn name, srid boolean, scale_x boolean, scale_y boolean, blocksize_x boolean, blocksize_y boolean, same_alignment boolean, regular_blocking boolean, num_bands boolean, pixel_types boolean, nodata_values boolean, out_db boolean, extent boolean) IS 'args: rasttable, rastcolumn, srid, scale_x, scale_y, blocksize_x, blocksize_y, same_alignment, regular_blocking, num_bands=true, pixel_types=true, nodata_values=true, out_db=true, extent=true - Adds raster constraints to a loaded raster table for a specific column that constrains spatial ref, scaling, blocksize, alignment, bands, band type and a flag to denote if raster column is regularly blocked. The table must be loaded with data for the constraints to be inferred. Returns true of the constraint setting was accomplished and if issues a notice.';
CREATE FUNCTION sqlex.checkauth(CLOB, CLOB) RETURNS integer
    LANGUAGE sql
    AS $_$ SELECT CheckAuth('', $1, $2) $_$;
CREATE FUNCTION sqlex.dropgeometrytable(table_name character varying) RETURNS CLOB
    LANGUAGE sql STRICT
    AS $_$ SELECT DropGeometryTable('','',$1) $_$;
CREATE FUNCTION sqlex.dropgeometrytable(schema_name character varying, table_name character varying) RETURNS CLOB
    LANGUAGE sql STRICT
    AS $_$ SELECT DropGeometryTable('',$1,$2) $_$;
CREATE FUNCTION sqlex.dropoverviewconstraints(ovtable name, ovcolumn name) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT DropOverviewConstraints('', $1, $2) $_$;
CREATE FUNCTION sqlex.droprasterconstraints(rasttable name, rastcolumn name, VARIADIC constraints CLOB[]) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT DropRasterConstraints('', $1, $2, VARIADIC $3) $_$;
CREATE FUNCTION sqlex.droprasterconstraints(rasttable name, rastcolumn name, srid boolean DEFAULT true, scale_x boolean DEFAULT true, scale_y boolean DEFAULT true, blocksize_x boolean DEFAULT true, blocksize_y boolean DEFAULT true, same_alignment boolean DEFAULT true, regular_blocking boolean DEFAULT true, num_bands boolean DEFAULT true, pixel_types boolean DEFAULT true, nodata_values boolean DEFAULT true, out_db boolean DEFAULT true, extent boolean DEFAULT true) RETURNS boolean
    LANGUAGE sql STRICT
    AS $_$ SELECT DropRasterConstraints('', $1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, $13, $14) $_$;
COMMENT ON FUNCTION sqlex.droprasterconstraints(rasttable name, rastcolumn name, srid boolean, scale_x boolean, scale_y boolean, blocksize_x boolean, blocksize_y boolean, same_alignment boolean, regular_blocking boolean, num_bands boolean, pixel_types boolean, nodata_values boolean, out_db boolean, extent boolean) IS 'args: rasttable, rastcolumn, srid, scale_x, scale_y, blocksize_x, blocksize_y, same_alignment, regular_blocking, num_bands=true, pixel_types=true, nodata_values=true, out_db=true, extent=true - Drops PostGIS raster constraints that refer to a raster table column. Useful if you need to reload data or update your raster column data.';
CREATE FUNCTION sqlex.lockrow(CLOB, CLOB, CLOB) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow(current_schema(), $1, $2, $3, now()::timestamp+'1:00'); $_$;
CREATE FUNCTION sqlex.lockrow(CLOB, CLOB, CLOB, CLOB) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow($1, $2, $3, $4, now()::timestamp+'1:00'); $_$;
CREATE FUNCTION sqlex.lockrow(CLOB, CLOB, CLOB, timestamp without time zone) RETURNS integer
    LANGUAGE sql STRICT
    AS $_$ SELECT LockRow(current_schema(), $1, $2, $3, $4); $_$;
CREATE FUNCTION sqlex.postgis_constraint_dims(geomschema CLOB, geomtable CLOB, geomcolumn CLOB) RETURNS integer
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
CREATE FUNCTION sqlex.postgis_constraint_srid(geomschema CLOB, geomtable CLOB, geomcolumn CLOB) RETURNS integer
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
CREATE FUNCTION sqlex.postgis_constraint_type(geomschema CLOB, geomtable CLOB, geomcolumn CLOB) RETURNS character varying
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
CREATE FUNCTION sqlex.postgis_raster_scripts_installed() RETURNS CLOB
    LANGUAGE sql IMMUTABLE
    AS $$ SELECT '2.0.1'::CLOB || ' r' || 9979::CLOB AS version $$;
CREATE FUNCTION sqlex.postgis_scripts_build_date() RETURNS CLOB
    LANGUAGE sql IMMUTABLE
    AS $$SELECT '2012-11-16 18:39:39'::CLOB AS version$$;
CREATE FUNCTION sqlex.postgis_scripts_installed() RETURNS CLOB
    LANGUAGE sql IMMUTABLE
    AS $$ SELECT '2.0.1'::CLOB || ' r' || 9979::CLOB AS version $$;
CREATE FUNCTION sqlex.postgis_topology_scripts_installed() RETURNS CLOB
    LANGUAGE sql IMMUTABLE
    AS $$ SELECT '2.0.1'::CLOB || ' r' || 9979::CLOB AS version $$;
CREATE FUNCTION sqlex.postgis_type_name(geomname character varying, coord_dimension integer, use_new_name boolean DEFAULT true) RETURNS character varying
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
CREATE FUNCTION sqlex.st_approxcount(rastertable CLOB, rastercolumn CLOB, sample_percent double precision) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, 1, TRUE, $3) $_$;
CREATE FUNCTION sqlex.st_approxcount(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean, sample_percent double precision DEFAULT 0.1) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, 1, $3, $4) $_$;
CREATE FUNCTION sqlex.st_approxcount(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, $3, TRUE, $4) $_$;
CREATE FUNCTION sqlex.st_approxcount(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, $3, $4, $5) $_$;
CREATE FUNCTION sqlex.st_approxhistogram(rastertable CLOB, rastercolumn CLOB, sample_percent double precision) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, 1, TRUE, $3, 0, NULL, FALSE) $_$;
CREATE FUNCTION sqlex.st_approxhistogram(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, $4, 0, NULL, FALSE) $_$;
CREATE FUNCTION sqlex.st_approxhistogram(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision, bins integer, "right" boolean) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, $4, $5, NULL, $6) $_$;
CREATE FUNCTION sqlex.st_approxhistogram(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, sample_percent double precision, bins integer, "right" boolean) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, $5, $6, NULL, $7) $_$;
CREATE FUNCTION sqlex.st_approxhistogram(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision, bins integer, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, $4, $5, $6, $7) $_$;
CREATE FUNCTION sqlex.st_approxhistogram(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1, bins integer DEFAULT 0, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, $5, $6, $7, $8) $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, quantiles double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_quantile($1, $2, 1, TRUE, 0.1, $3) $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE
    AS $_$ SELECT (_st_quantile($1, $2, 1, TRUE, 0.1, ARRAY[$3]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean, quantile double precision DEFAULT NULL::double precision) RETURNS double precision
    LANGUAGE sql STABLE
    AS $_$ SELECT (_st_quantile($1, $2, 1, $3, 0.1, ARRAY[$4]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, sample_percent double precision, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, 1, TRUE, $3, $4) $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, sample_percent double precision, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, 1, TRUE, $3, ARRAY[$4]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, $3, TRUE, $4, $5) $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, TRUE, $4, ARRAY[$5]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, $3, $4, $5, $6) $_$;
CREATE FUNCTION sqlex.st_approxquantile(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, sample_percent double precision, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, $4, $5, ARRAY[$6]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_approxsummarystats(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean) RETURNS sqlex.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, 1, $3, 0.1) $_$;
CREATE FUNCTION sqlex.st_approxsummarystats(rastertable CLOB, rastercolumn CLOB, sample_percent double precision) RETURNS sqlex.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, 1, TRUE, $3) $_$;
CREATE FUNCTION sqlex.st_approxsummarystats(rastertable CLOB, rastercolumn CLOB, nband integer, sample_percent double precision) RETURNS sqlex.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, $3, TRUE, $4) $_$;
CREATE FUNCTION sqlex.st_approxsummarystats(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, sample_percent double precision DEFAULT 0.1) RETURNS sqlex.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, $3, $4, $5) $_$;
CREATE FUNCTION sqlex.st_area(CLOB) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Area($1::geometry);  $_$;
CREATE FUNCTION sqlex.st_asewkt(CLOB) RETURNS CLOB
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsEWKT($1::geometry);  $_$;
CREATE FUNCTION sqlex.st_asgeojson(CLOB) RETURNS CLOB
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_AsGeoJson(1, $1::geometry,15,0);  $_$;
CREATE FUNCTION sqlex.st_asgml(CLOB) RETURNS CLOB
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_AsGML(2,$1::geometry,15,0, NULL);  $_$;
CREATE FUNCTION sqlex.st_askml(CLOB) RETURNS CLOB
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT _ST_AsKML(2, $1::geometry, 15, null);  $_$;
CREATE FUNCTION sqlex.st_assvg(CLOB) RETURNS CLOB
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsSVG($1::geometry,0,15);  $_$;
CREATE FUNCTION sqlex.st_asCLOB(CLOB) RETURNS CLOB
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_AsCLOB($1::geometry);  $_$;
CREATE FUNCTION sqlex.st_count(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, 1, $3, 1) $_$;
COMMENT ON FUNCTION sqlex.st_count(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, exclude_nodata_value - Returns the number of pixels in a given band of a raster or raster coverage. If no band is specified defaults to band 1. If exclude_nodata_value is set to true, will  count pixels that are not equal to the nodata value.';
CREATE FUNCTION sqlex.st_count(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true) RETURNS bigint
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_count($1, $2, $3, $4, 1) $_$;
COMMENT ON FUNCTION sqlex.st_count(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true - Returns the number of pixels in a given band of a raster or raster coverage. If no band is specified defaults to band 1. If exclude_nodata_value is set to true, will  count pixels that are not equal to the nodata value.';
CREATE FUNCTION sqlex.st_coveredby(CLOB, CLOB) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_CoveredBy($1::geometry, $2::geometry);  $_$;
CREATE FUNCTION sqlex.st_covers(CLOB, CLOB) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_Covers($1::geometry, $2::geometry);  $_$;
CREATE FUNCTION sqlex.st_distance(CLOB, CLOB) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Distance($1::geometry, $2::geometry);  $_$;
CREATE FUNCTION sqlex.st_distinct4ma(matrix double precision[], nodatamode CLOB, VARIADIC args CLOB[]) RETURNS double precision
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT COUNT(DISTINCT unnest)::float FROM unnest($1) $_$;
COMMENT ON FUNCTION sqlex.st_distinct4ma(matrix double precision[], nodatamode CLOB, VARIADIC args CLOB[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the number of unique pixel values in a neighborhood.';
CREATE FUNCTION sqlex.st_dwithin(CLOB, CLOB, double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_DWithin($1::geometry, $2::geometry, $3);  $_$;
CREATE FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, bins integer, "right" boolean) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, 1, $4, NULL, $5) $_$;
COMMENT ON FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, bins integer, "right" boolean) IS 'args: rastertable, rastercolumn, nband, bins, right - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';
CREATE FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, bins integer, "right" boolean) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, 1, $5, NULL, $6) $_$;
COMMENT ON FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, bins integer, "right" boolean) IS 'args: rastertable, rastercolumn, nband, exclude_nodata_value, bins, right - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';
CREATE FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, bins integer, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_histogram($1, $2, $3, TRUE, 1, $4, $5, $6) $_$;
COMMENT ON FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, bins integer, width double precision[], "right" boolean) IS 'args: rastertable, rastercolumn, nband=1, bins, width=NULL, right=false - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';
CREATE FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, bins integer DEFAULT 0, width double precision[] DEFAULT NULL::double precision[], "right" boolean DEFAULT false) RETURNS SETOF sqlex.histogram
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_histogram($1, $2, $3, $4, 1, $5, $6, $7) $_$;
COMMENT ON FUNCTION sqlex.st_histogram(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, bins integer, width double precision[], "right" boolean) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true, bins=autocomputed, width=NULL, right=false - Returns a set of histogram summarizing a raster or raster coverage data distribution separate bin ranges. Number of bins are autocomputed if not specified.';
CREATE FUNCTION sqlex.st_intersects(CLOB, CLOB) RETURNS boolean
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT ST_Intersects($1::geometry, $2::geometry);  $_$;
CREATE FUNCTION sqlex.st_length(CLOB) RETURNS double precision
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT ST_Length($1::geometry);  $_$;
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, quantiles double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_quantile($1, $2, 1, TRUE, 1, $3) $_$;
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, 1, TRUE, 1, ARRAY[$3]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean, quantile double precision DEFAULT NULL::double precision) RETURNS double precision
    LANGUAGE sql STABLE
    AS $_$ SELECT (_st_quantile($1, $2, 1, $3, 1, ARRAY[$4]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, nband integer, quantiles double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_quantile($1, $2, $3, TRUE, 1, $4) $_$;
COMMENT ON FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, nband integer, quantiles double precision[]) IS 'args: rastertable, rastercolumn, nband, quantiles - Compute quantiles for a raster or raster table coverage in the conCLOB of the sample or population. Thus, a value could be examined to be at the rasters 25%, 50%, 75% percentile.';
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, nband integer, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, TRUE, 1, ARRAY[$4]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, quantiles double precision[] DEFAULT NULL::double precision[]) RETURNS SETOF sqlex.quantile
    LANGUAGE sql STABLE
    AS $_$ SELECT _st_quantile($1, $2, $3, $4, 1, $5) $_$;
COMMENT ON FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, quantiles double precision[]) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true, quantiles=NULL - Compute quantiles for a raster or raster table coverage in the conCLOB of the sample or population. Thus, a value could be examined to be at the rasters 25%, 50%, 75% percentile.';
CREATE FUNCTION sqlex.st_quantile(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, quantile double precision) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_quantile($1, $2, $3, $4, 1, ARRAY[$5]::double precision[])).value $_$;
CREATE FUNCTION sqlex.st_samealignment(ulx1 double precision, uly1 double precision, scalex1 double precision, scaley1 double precision, skewx1 double precision, skewy1 double precision, ulx2 double precision, uly2 double precision, scalex2 double precision, scaley2 double precision, skewx2 double precision, skewy2 double precision) RETURNS boolean
    LANGUAGE sql IMMUTABLE STRICT
    AS $_$ SELECT st_samealignment(st_makeemptyraster(1, 1, $1, $2, $3, $4, $5, $6), st_makeemptyraster(1, 1, $7, $8, $9, $10, $11, $12)) $_$;
COMMENT ON FUNCTION sqlex.st_samealignment(ulx1 double precision, uly1 double precision, scalex1 double precision, scaley1 double precision, skewx1 double precision, skewy1 double precision, ulx2 double precision, uly2 double precision, scalex2 double precision, scaley2 double precision, skewx2 double precision, skewy2 double precision) IS 'args: ulx1, uly1, scalex1, scaley1, skewx1, skewy1, ulx2, uly2, scalex2, scaley2, skewx2, skewy2 - Returns true if rasters have same skew, scale, spatial ref and false if they dont with notice detailing issue.';
CREATE FUNCTION sqlex.st_stddev4ma(matrix double precision[], nodatamode CLOB, VARIADIC args CLOB[]) RETURNS double precision
    LANGUAGE sql IMMUTABLE
    AS $_$ SELECT stddev(unnest) FROM unnest($1) $_$;
COMMENT ON FUNCTION sqlex.st_stddev4ma(matrix double precision[], nodatamode CLOB, VARIADIC args CLOB[]) IS 'args: matrix, nodatamode, VARIADIC args - Raster processing function that calculates the standard deviation of pixel values in a neighborhood.';
CREATE FUNCTION sqlex.st_summarystats(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean) RETURNS sqlex.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, 1, $3, 1) $_$;
COMMENT ON FUNCTION sqlex.st_summarystats(rastertable CLOB, rastercolumn CLOB, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, exclude_nodata_value - Returns summary stats consisting of count,sum,mean,stddev,min,max for a given raster band of a raster or raster coverage. Band 1 is assumed is no band is specified.';
CREATE FUNCTION sqlex.st_summarystats(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true) RETURNS sqlex.summarystats
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT _st_summarystats($1, $2, $3, $4, 1) $_$;
COMMENT ON FUNCTION sqlex.st_summarystats(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true - Returns summary stats consisting of count,sum,mean,stddev,min,max for a given raster band of a raster or raster coverage. Band 1 is assumed is no band is specified.';
CREATE FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT count integer) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, count FROM _st_valuecount($1, $2, 1, TRUE, $3, $4) $_$;
COMMENT ON FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, searchvalues double precision[], roundto double precision, OUT value double precision, OUT count integer) IS 'args: rastertable, rastercolumn, searchvalues, roundto=0, OUT value, OUT count - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';
CREATE FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, 1, TRUE, ARRAY[$3]::double precision[], $4)).count $_$;
COMMENT ON FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, searchvalue double precision, roundto double precision) IS 'args: rastertable, rastercolumn, searchvalue, roundto=0 - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';
CREATE FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT count integer) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, count FROM _st_valuecount($1, $2, $3, TRUE, $4, $5) $_$;
COMMENT ON FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, searchvalues double precision[], roundto double precision, OUT value double precision, OUT count integer) IS 'args: rastertable, rastercolumn, nband, searchvalues, roundto=0, OUT value, OUT count - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';
CREATE FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, TRUE, ARRAY[$4]::double precision[], $5)).count $_$;
COMMENT ON FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, searchvalue double precision, roundto double precision) IS 'args: rastertable, rastercolumn, nband, searchvalue, roundto=0 - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';
CREATE FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, searchvalues double precision[] DEFAULT NULL::double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT count integer) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, count FROM _st_valuecount($1, $2, $3, $4, $5, $6) $_$;
COMMENT ON FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, searchvalues double precision[], roundto double precision, OUT value double precision, OUT count integer) IS 'args: rastertable, rastercolumn, nband=1, exclude_nodata_value=true, searchvalues=NULL, roundto=0, OUT value, OUT count - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';
CREATE FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS integer
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, $4, ARRAY[$5]::double precision[], $6)).count $_$;
COMMENT ON FUNCTION sqlex.st_valuecount(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, searchvalue double precision, roundto double precision) IS 'args: rastertable, rastercolumn, nband, exclude_nodata_value, searchvalue, roundto=0 - Returns a set of records containing a pixel band value and count of the number of pixels in a given band of a raster (or a raster coverage) that have a given set of values. If no band is specified defaults to band 1. By default nodata value pixels are not counted. and all other values in the pixel are output and pixel band values are rounded to the nearest integer.';
CREATE FUNCTION sqlex.st_valuepercent(rastertable CLOB, rastercolumn CLOB, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT percent double precision) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, percent FROM _st_valuecount($1, $2, 1, TRUE, $3, $4) $_$;
CREATE FUNCTION sqlex.st_valuepercent(rastertable CLOB, rastercolumn CLOB, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, 1, TRUE, ARRAY[$3]::double precision[], $4)).percent $_$;
CREATE FUNCTION sqlex.st_valuepercent(rastertable CLOB, rastercolumn CLOB, nband integer, searchvalues double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT percent double precision) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, percent FROM _st_valuecount($1, $2, $3, TRUE, $4, $5) $_$;
CREATE FUNCTION sqlex.st_valuepercent(rastertable CLOB, rastercolumn CLOB, nband integer, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, TRUE, ARRAY[$4]::double precision[], $5)).percent $_$;
CREATE FUNCTION sqlex.st_valuepercent(rastertable CLOB, rastercolumn CLOB, nband integer DEFAULT 1, exclude_nodata_value boolean DEFAULT true, searchvalues double precision[] DEFAULT NULL::double precision[], roundto double precision DEFAULT 0, OUT value double precision, OUT percent double precision) RETURNS SETOF record
    LANGUAGE sql STABLE
    AS $_$ SELECT value, percent FROM _st_valuecount($1, $2, $3, $4, $5, $6) $_$;
CREATE FUNCTION sqlex.st_valuepercent(rastertable CLOB, rastercolumn CLOB, nband integer, exclude_nodata_value boolean, searchvalue double precision, roundto double precision DEFAULT 0) RETURNS double precision
    LANGUAGE sql STABLE STRICT
    AS $_$ SELECT (_st_valuecount($1, $2, $3, $4, ARRAY[$5]::double precision[], $6)).percent $_$;
-
CREATE OPERATOR FAMILY sqlex.btree_geography_ops USING btree;
CREATE OPERATOR FAMILY sqlex.btree_geometry_ops USING btree;
CREATE OPERATOR FAMILY sqlex.gist_geography_ops USING gist;
CREATE OPERATOR FAMILY sqlex.gist_geometry_ops_2d USING gist;
CREATE OPERATOR FAMILY sqlex.gist_geometry_ops_nd USING gist;
SET default_tablespace = '';
SET default_with_oids = false;
CREATE TABLE sqlex.a (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.abc (
    emp_dept integer,
    emp_idno integer,
    rnk2 bigint
);
CREATE TABLE sqlex.actor (
    act_id integer NOT NULL,
    act_fname character(20),
    act_lname character(20),
    act_gender character(1)
);
CREATE TABLE sqlex.actorsbackup2017 (
    act_fname character(20),
    act_lname character(20)
);
CREATE TABLE sqlex.address (
    city character varying(15)
);
CREATE TABLE sqlex.affiliated_with (
    physician integer NOT NULL,
    department integer NOT NULL,
    primaryaffiliation boolean NOT NULL
);
CREATE TABLE sqlex.customer (
    customer_id numeric(5,0) NOT NULL,
    cust_name character varying(30) NOT NULL,
    city character varying(15),
    grade numeric(3,0) DEFAULT 0,
    salesman_id numeric(5,0) NOT NULL
);
CREATE VIEW sqlex.agentview AS
 SELECT customer.customer_id,
    customer.cust_name,
    customer.city,
    customer.grade,
    customer.salesman_id
   FROM sqlex.customer;
CREATE TABLE sqlex.ahmed (
    employee_id numeric(6,0),
    department_id numeric(4,0),
    salary numeric(8,2)
);
CREATE TABLE sqlex.appointment (
    appointmentid integer NOT NULL,
    patient integer NOT NULL,
    prepnurse integer,
    physician integer NOT NULL,
    start_dt_time timestamp without time zone NOT NULL,
    end_dt_time timestamp without time zone NOT NULL,
    examinationroom CLOB NOT NULL
);
CREATE TABLE sqlex.asst_referee_mast (
    ass_ref_id numeric NOT NULL,
    ass_ref_name character varying(40) NOT NULL,
    country_id numeric NOT NULL
);
CREATE TABLE sqlex.bh (
   char(10)character varying(20) NOT NULL,
    emp_id integer NOT NULL,
    process character varying(20)
);
CREATE TABLE sqlex.bitch (
    sum bigint
);
CREATE TABLE sqlex.blah (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.block (
    blockfloor integer NOT NULL,
    blockcode integer NOT NULL
);
CREATE TABLE sqlex.casino (
    pricerange numeric(6,0) NOT NULL,
    events character varying(50),
    location character varying(40) NOT NULL,
    casino character varying(75) NOT NULL
);
CREATE TABLE sqlex.coach_mast (
    coach_id numeric NOT NULL,
    coach_name character varying(40) NOT NULL
);
CREATE TABLE sqlex.col1 (
    "?column?" integer
);
CREATE TABLE sqlex.company_mast (
    com_id integer NOT NULL,
    com_name character varying(20) NOT NULL
);
CREATE TABLE sqlex.countries (
    country_id character varying(2),
    country_name character varying(40),
    region_id numeric(10,0)
);
CREATE TABLE sqlex.customer_backup (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.customer_id (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.customer_id_123 (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);
CREATE VIEW sqlex.customergradelevels AS
 SELECT DISTINCT customer.grade,
    count(*) AS count
   FROM sqlex.customer
  GROUP BY customer.grade;
CREATE VIEW sqlex.customergradelevels2 AS
 SELECT DISTINCT customer.grade,
    count(*) AS count
   FROM sqlex.customer
  WHERE (customer.grade IS NOT NULL)
  GROUP BY customer.grade;
CREATE TABLE sqlex.department (
    departmentid integer NOT NULL,
   char(10)CLOB NOT NULL,
    head integer NOT NULL
);
CREATE TABLE sqlex.department_detail (
    first_name character varying(20),
    last_name character varying(25),
    department_id numeric(4,0),
    department_name character varying(30)
);
CREATE TABLE sqlex.departments (
    department_id numeric(4,0) NOT NULL,
    department_name character varying(30) NOT NULL,
    manager_id numeric(6,0) DEFAULT NULL::numeric,
    location_id numeric(4,0) DEFAULT NULL::numeric
);
CREATE TABLE sqlex.director (
    dir_id integer NOT NULL,
    dir_fname character(20),
    dir_lname character(20)
);
CREATE TABLE sqlex.duplicate (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.elephants (
    id integer NOT NULL,
   char(10)CLOB,
    date date DEFAULT now()
);
CREATE TABLE sqlex.emp (
    ename character varying(13),
    salary numeric(6,0),
    eid numeric(3,0) NOT NULL
);
CREATE TABLE sqlex.emp_department (
    dpt_code integer NOT NULL,
    dpt_name character(15) NOT NULL,
    dpt_allotment integer NOT NULL
);
CREATE TABLE sqlex.emp_details (
    emp_idno integer NOT NULL,
    emp_fname character(15) NOT NULL,
    emp_lname character(15) NOT NULL,
    emp_dept integer NOT NULL
);
CREATE TABLE sqlex.employee (
    empno integer,
    ename character varying(20)
);
CREATE TABLE sqlex.employees (
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
CREATE TABLE sqlex.ff (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2),
    year integer,
    subject character(25),
    winner character(45),
    country character(25),
    category character(25)
);
CREATE TABLE sqlex.fg (
    customer_id numeric(5,0),
    avg numeric
);
CREATE TABLE sqlex.game_scores (
    id integer NOT NULL,
   char(10)CLOB,
    score integer
);
CREATE TABLE sqlex.genres (
    gen_id integer NOT NULL,
    gen_title character(20)
);
CREATE TABLE sqlex.goal_details (
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
CREATE TABLE sqlex.grade (
    city character varying(15)
);
CREATE VIEW sqlex.grade_customer AS
 SELECT customer.grade,
    count(customer.customer_id) AS count
   FROM sqlex.customer
  GROUP BY customer.grade;
CREATE VIEW sqlex.grade_customer1 AS
 SELECT customer.grade,
    count(*) AS number
   FROM sqlex.customer
  GROUP BY customer.grade;
CREATE TABLE sqlex.grades (
    id integer NOT NULL,
    grade_1 integer,
    grade_2 integer,
    grade_3 integer
);
CREATE TABLE sqlex.hello (
    number_one integer,
    number_two integer,
    number_three integer
);
CREATE TABLE sqlex.hello1_1122 (
    abc integer
);
CREATE TABLE sqlex.hello1_12 (
    abc integer
);
CREATE TABLE sqlex.hello_12 (
    abc integer
);
CREATE TABLE sqlex.item_mast (
    pro_id integer NOT NULL,
    pro_name character varying(25) NOT NULL,
    pro_price numeric(8,2) NOT NULL,
    pro_com integer NOT NULL
);
CREATE TABLE sqlex.job_grades (
    grade_level character varying(20) NOT NULL,
    lowest_sal numeric(5,0) NOT NULL,
    highest_sal numeric(5,0) NOT NULL
);
CREATE TABLE sqlex.job_history (
    employee_id numeric(6,0) NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL,
    job_id character varying(10) NOT NULL,
    department_id numeric(4,0) DEFAULT NULL::numeric
);
CREATE TABLE sqlex.jobs (
    job_id character varying(10) DEFAULT ''::character varying NOT NULL,
    job_title character varying(35) NOT NULL,
    min_salary numeric(6,0) DEFAULT NULL::numeric,
    max_salary numeric(6,0) DEFAULT NULL::numeric
);
CREATE TABLE sqlex.kk (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.kkk (
   char(10)character varying(30)
);
CREATE TABLE sqlex.locations (
    location_id numeric(4,0) DEFAULT (0)::numeric NOT NULL,
    street_address character varying(40) DEFAULT NULL::character varying,
    postal_code character varying(12) DEFAULT NULL::character varying,
    city character varying(30) NOT NULL,
    state_province character varying(25) DEFAULT NULL::character varying,
    country_id character varying(2) DEFAULT NULL::character varying
);
CREATE TABLE sqlex.londoncustomers (
    cust_name character varying(30),
    city character varying(15)
);
CREATE TABLE sqlex.manufacturers (
    code integer NOT NULL,
   char(10)character varying(255) NOT NULL
);
CREATE TABLE sqlex.match_captain (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_captain numeric NOT NULL
);
CREATE TABLE sqlex.match_details (
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
CREATE TABLE sqlex.match_mast (
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
CREATE TABLE sqlex.maxim00 (
    num integer,
   char(10)character varying(10)
);
CREATE TABLE sqlex.maximum (
    num integer,
   char(10)character varying(10)
);
CREATE TABLE sqlex.maximum00 (
    num integer,
   char(10)character varying(10)
);
CREATE TABLE sqlex.maximum899 (
    num integer,
   char(10)character varying(10)
);
CREATE TABLE sqlex.medication (
    code integer NOT NULL,
   char(10)CLOB NOT NULL,
    brand CLOB NOT NULL,
    description CLOB NOT NULL
);
CREATE TABLE sqlex.movie (
    mov_id integer NOT NULL,
    mov_title character(50),
    mov_year integer,
    mov_time integer,
    mov_lang character(15),
    mov_dt_rel date,
    mov_rel_country character(5)
);
CREATE TABLE sqlex.movie_cast (
    act_id integer NOT NULL,
    mov_id integer NOT NULL,
    role character(30)
);
CREATE TABLE sqlex.movie_direction (
    dir_id integer NOT NULL,
    mov_id integer NOT NULL
);
CREATE TABLE sqlex.movie_genres (
    mov_id integer NOT NULL,
    gen_id integer NOT NULL
);
CREATE TABLE sqlex.my (
    customer_id numeric(5,0),
    avg numeric
);
CREATE TABLE sqlex.mytemptable (
    customer_id numeric(5,0),
    avg numeric
);
CREATE TABLE sqlex.mytest (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);
CREATE TABLE sqlex.mytest1 (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);
CREATE TABLE sqlex.salesman (
    salesman_id numeric(5,0) NOT NULL,
   char(10)character varying(30) NOT NULL,
    city character varying(15),
    commission numeric(5,2)
);
CREATE VIEW sqlex.myworkstuff AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman;
CREATE VIEW sqlex.myworkstuffs AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman
  WHERE ((salesman.city)::CLOB = 'New York'::CLOB);
CREATE TABLE sqlex.new (
    city character varying(15)
);
CREATE TABLE sqlex.new123 (
    "Customer" character varying(30),
    city character varying(15),
    "Salesman" character varying(30),
    commission numeric(5,2)
);
CREATE TABLE sqlex.new_table (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.newsalesman (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.newtab (
    ord_no numeric(5,0),
    purch_amt numeric(8,2),
    ord_date date,
    customer_id numeric(5,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.newtable (
    roomnumber integer,
    roomtype character varying(30),
    blockfloor integer,
    blockcode integer,
    unavailable boolean
);
CREATE VIEW sqlex.newyorksalesman AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM sqlex.salesman
  WHERE ((salesman.city)::CLOB = 'new york'::CLOB);
CREATE VIEW sqlex.newyorksalesman2 AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM sqlex.salesman
  WHERE ((salesman.city)::CLOB = 'rome'::CLOB);
CREATE VIEW sqlex.newyorksalesman3 AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM sqlex.salesman
  WHERE ((salesman.city)::CLOB = 'Rome'::CLOB);
CREATE VIEW sqlex.newyorkstaff AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city,
    salesman.commission
   FROM sqlex.salesman
  WHERE ((salesman.city)::CLOB = 'New York'::CLOB);
CREATE TABLE sqlex.nobel_win (
    year integer,
    subject character(25),
    winner character(45),
    country character(25),
    category character(25)
);
CREATE TABLE sqlex.orders (
    ord_no numeric(5,0) NOT NULL,
    purch_amt numeric(8,2) DEFAULT 0,
    ord_date date,
    customer_id numeric(5,0) NOT NULL,
    salesman_id numeric(5,0) NOT NULL
);
CREATE VIEW sqlex.norders AS
 SELECT salesman.name,
    avg(orders.purch_amt) AS avg,
    sum(orders.purch_amt) AS sum
   FROM sqlex.salesman,
    sqlex.orders
  WHERE (salesman.salesman_id = orders.salesman_id)
  GROUP BY salesman.name;
CREATE TABLE sqlex.nros (
    uno integer,
    dos integer
);
CREATE TABLE sqlex.nuevo (
    city character varying(15)
);
CREATE TABLE sqlex.numbers (
    one integer,
    two integer,
    three integer
);
CREATE TABLE sqlex.numeri (
    id integer,
    data date,
    decimali real
);
CREATE TABLE sqlex.numeros (
    uno integer,
    dos integer
);
CREATE TABLE sqlex.nurse (
    employeeid integer NOT NULL,
   char(10)CLOB NOT NULL,
    "position" CLOB NOT NULL,
    registered boolean NOT NULL,
    ssn integer NOT NULL
);
CREATE VIEW sqlex.odr AS
 SELECT orders.ord_no
   FROM sqlex.orders
  WHERE ((orders.ord_no >= (70002)::numeric) AND (orders.ord_no <= (70008)::numeric));
CREATE TABLE sqlex.oi (
    customer_id numeric(5,0),
    avg numeric
);
CREATE TABLE sqlex.on_call (
    nurse integer NOT NULL,
    blockfloor integer NOT NULL,
    blockcode integer NOT NULL,
    oncallstart timestamp without time zone NOT NULL,
    oncallend timestamp without time zone NOT NULL
);
CREATE VIEW sqlex.ordersview AS
 SELECT orders.ord_no,
    orders.purch_amt
   FROM sqlex.orders
  WHERE (orders.purch_amt < (500)::numeric);
CREATE TABLE sqlex.orozco (
    city character varying(15)
);
CREATE TABLE sqlex.partest1 (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);
CREATE TABLE sqlex.participant (
    participant_id integer NOT NULL,
    part_name character varying(20) NOT NULL
);
CREATE TABLE sqlex.participants (
    participant_id integer NOT NULL,
    part_name character varying(20) NOT NULL
);
CREATE TABLE sqlex.patient (
    ssn integer NOT NULL,
   char(10)CLOB NOT NULL,
    address CLOB NOT NULL,
    phone CLOB NOT NULL,
    insuranceid integer NOT NULL,
    pcp integer NOT NULL
);
CREATE TABLE sqlex.penalty_gk (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_gk numeric NOT NULL
);
CREATE TABLE sqlex.penalty_shootout (
    kick_id numeric NOT NULL,
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_id numeric NOT NULL,
    score_goal character(1) NOT NULL,
    kick_no numeric NOT NULL
);
CREATE TABLE sqlex.persons (
    personid integer,
    lastname character varying(255),
    firstname character varying(255),
    address character varying(255),
    city character varying(255)
);
CREATE TABLE sqlex.physician (
    employeeid integer NOT NULL,
   char(10)CLOB NOT NULL,
    "position" CLOB NOT NULL,
    ssn integer NOT NULL
);
CREATE TABLE sqlex.player_booked (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_id numeric NOT NULL,
    booking_time numeric NOT NULL,
    sent_off character(1) DEFAULT NULL::bpchar,
    play_schedule character(2) NOT NULL,
    play_half numeric NOT NULL
);
CREATE TABLE sqlex.player_in_out (
    match_no numeric NOT NULL,
    team_id numeric NOT NULL,
    player_id numeric NOT NULL,
    in_out character(1) NOT NULL,
    time_in_out numeric NOT NULL,
    play_schedule character(2) NOT NULL,
    play_half numeric NOT NULL
);
CREATE TABLE sqlex.player_mast (
    player_id numeric NOT NULL,
    team_id numeric NOT NULL,
    jersey_no numeric NOT NULL,
    player_name character varying(40) NOT NULL,
    posi_to_play character(2) NOT NULL,
    dt_of_bir date,
    age numeric,
    playing_club character varying(40)
);
CREATE TABLE sqlex.playing_position (
    position_id character(2) NOT NULL,
    position_desc character varying(15) NOT NULL
);
CREATE TABLE sqlex.prescribes (
    physician integer NOT NULL,
    patient integer NOT NULL,
    medication integer NOT NULL,
    date timestamp without time zone NOT NULL,
    appointment integer,
    dose CLOB NOT NULL
);
CREATE TABLE sqlex.procedure (
    code integer NOT NULL,
   char(10)CLOB NOT NULL,
    cost real NOT NULL
);
CREATE VIEW sqlex.raster_overviews AS
 SELECT current_database() AS o_table_catalog,
    n.nspname AS o_table_schema,
    c.relname AS o_table_name,
    a.attname AS o_raster_column,
    current_database() AS r_table_catalog,
    (split_part(split_part(s.consrc, '''::name'::CLOB, 1), ''''::CLOB, 2))::name AS r_table_schema,
    (split_part(split_part(s.consrc, '''::name'::CLOB, 2), ''''::CLOB, 2))::name AS r_table_name,
    (split_part(split_part(s.consrc, '''::name'::CLOB, 3), ''''::CLOB, 2))::name AS r_raster_column,
    (btrim(split_part(s.consrc, ','::CLOB, 2)))::integer AS overview_factor
   FROM pg_class c,
    pg_attribute a,
    pg_type t,
    pg_namespace n,
    pg_constraint s
  WHERE ((((((((((t.typname = 'raster'::name) AND (a.attisdropped = false)) AND (a.atttypid = t.oid)) AND (a.attrelid = c.oid)) AND (c.relnamespace = n.oid)) AND ((c.relkind = 'r'::"char") OR (c.relkind = 'v'::"char"))) AND (s.connamespace = n.oid)) AND (s.conrelid = c.oid)) AND (s.consrc ~~ '%_overview_constraint(%'::CLOB)) AND (NOT pg_is_other_temp_schema(c.relnamespace)));
CREATE TABLE sqlex.rating (
    mov_id integer NOT NULL,
    rev_id integer NOT NULL,
    rev_stars numeric(4,2),
    num_o_ratings integer
);
CREATE TABLE sqlex.referee_mast (
    referee_id numeric NOT NULL,
    referee_name character varying(40) NOT NULL,
    country_id numeric NOT NULL
);
CREATE TABLE sqlex.regions (
    region_id numeric(10,0) NOT NULL,
    region_name character(25)
);
CREATE TABLE sqlex.related (
    city character varying(15)
);
CREATE TABLE sqlex.reviewer (
    rev_id integer NOT NULL,
    rev_name character(30)
);
CREATE VIEW sqlex.rightjoins AS
 SELECT t1.customer_id,
    t1.grade,
    t2.salesman_id
   FROM (sqlex.customer t1
     RIGHT JOIN sqlex.orders t2 ON ((t1.customer_id = t2.customer_id)))
  WHERE (t1.grade IS NOT NULL)
  ORDER BY t1.customer_id;
CREATE TABLE sqlex.room (
    roomnumber integer NOT NULL,
    roomtype character varying(30) NOT NULL,
    blockfloor integer NOT NULL,
    blockcode integer NOT NULL,
    unavailable boolean NOT NULL
);
CREATE VIEW sqlex.salesdetail AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.name
   FROM sqlex.salesman;
CREATE VIEW sqlex.salesman_detail AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.name
   FROM sqlex.salesman;
CREATE TABLE sqlex.salesman_do1304 (
    salesman_id character varying(20),
   char(10)character varying(40),
    city character varying(30),
    commission character varying(10)
);
CREATE VIEW sqlex.salesman_example AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman;
CREATE VIEW sqlex.salesman_ny AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.commission
   FROM sqlex.salesman
  WHERE (((salesman.city)::CLOB = 'New York'::CLOB) AND (salesman.commission > 0.13));
CREATE VIEW sqlex.salesmandetail AS
 SELECT salesman.salesman_id,
    salesman.city,
    salesman.name
   FROM sqlex.salesman;
CREATE VIEW sqlex.salesown AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman;
CREATE TABLE sqlex.sample_table (
    salesman_id character(4),
   char(10)character varying(20),
    city character varying(20),
    commission character(10)
);
CREATE TABLE sqlex.scores (
    id integer NOT NULL,
    score integer
);
CREATE TABLE sqlex.soccer_city (
    city_id numeric NOT NULL,
    city character varying(25) NOT NULL,
    country_id numeric NOT NULL
);
CREATE TABLE sqlex.soccer_country (
    country_id numeric NOT NULL,
    country_abbr character varying(4) NOT NULL,
    country_name character varying(40) NOT NULL
);
CREATE TABLE sqlex.soccer_team (
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
CREATE TABLE sqlex.soccer_venue (
    venue_id numeric NOT NULL,
    venue_name character varying(30) NOT NULL,
    city_id numeric NOT NULL,
    aud_capacity numeric NOT NULL
);
CREATE TABLE sqlex.statements (
   char(10)CLOB
);
CREATE TABLE sqlex.stay (
    stayid integer NOT NULL,
    patient integer NOT NULL,
    room integer NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone NOT NULL
);
CREATE TABLE sqlex.string (
    CLOB CLOB
);
CREATE TABLE sqlex.student (
    roll integer,
   char(10)character varying
);
CREATE TABLE sqlex.student1 (
    roll integer,
   char(10)character varying
);
CREATE TABLE sqlex.sybba (
    ord_num numeric(6,0) NOT NULL,
    ord_amount numeric(12,2),
    ord_date date NOT NULL,
    cust_code character(6) NOT NULL,
    agent_code character(6) NOT NULL
);
CREATE TABLE sqlex.table1 (
    "Customer Name" character varying(30),
    city character varying(15),
    "Salesman" character varying(30),
    commission numeric(5,2)
);
CREATE TABLE sqlex.team_coaches (
    team_id numeric NOT NULL,
    coach_id numeric NOT NULL
);
CREATE TABLE sqlex.temp (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.tempa (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.tempcustomer (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.temphi (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.tempp (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.tempp11 (
    customer_id numeric(5,0),
    cust_name character varying(30),
    city character varying(15),
    grade numeric(3,0),
    salesman_id numeric(5,0)
);
CREATE TABLE sqlex.tempsalesman (
    salesman_id numeric(5,0),
   char(10)character varying(30),
    city character varying(15),
    commission numeric(5,2)
);
CREATE TABLE sqlex.test (
    x integer
);
CREATE TABLE sqlex.teste (
    salesman_id numeric(5,0),
    count bigint
);
CREATE TABLE sqlex.testtable (
    col1 character varying(30) NOT NULL
);
COMMENT ON TABLE sqlex.testtable IS 'a temporary table for some queries ';
CREATE VIEW sqlex.testtesing AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman
  WHERE ((salesman.city)::CLOB = 'New York'::CLOB);
CREATE VIEW sqlex.testtest AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman;
CREATE TABLE sqlex.trained_in (
    physician integer NOT NULL,
    treatment integer NOT NULL,
    certificationdate date NOT NULL,
    certificationexpires date NOT NULL
);
CREATE TABLE sqlex.trenta (
    numeri integer
);
CREATE TABLE sqlex.tt (
   name integer
);
CREATE TABLE sqlex.undergoes (
    patient integer NOT NULL,
    procedure integer NOT NULL,
    stay integer NOT NULL,
    date timestamp without time zone NOT NULL,
    physician integer NOT NULL,
    assistingnurse integer
);
CREATE VIEW sqlex.v1 AS
 SELECT o.ord_no,
    c.cust_name,
    s.name
   FROM ((sqlex.orders o
     JOIN sqlex.customer c ON ((o.customer_id = c.customer_id)))
     JOIN sqlex.salesman s ON ((o.salesman_id = s.salesman_id)));
CREATE VIEW sqlex.view AS
 SELECT salesman.salesman_id,
    salesman.name,
    salesman.city
   FROM sqlex.salesman;
CREATE TABLE sqlex.vowl (
    cust_name character varying(30),
    "substring" CLOB
);
CREATE TABLE sqlex.zebras (
    id integer NOT NULL,
    score integer,
    date date DEFAULT now()
);
CREATE TABLE sqlex.zz (
    customer_id numeric(5,0),
    avg numeric
);
ALTER TABLE  sqlex.actor
    ADD CONSTRAINT actor_pkey PRIMARY KEY (act_id);
ALTER TABLE  sqlex.affiliated_with
    ADD CONSTRAINT affiliated_with_pkey PRIMARY KEY (physician, department);
ALTER TABLE  sqlex.appointment
    ADD CONSTRAINT appointment_pkey PRIMARY KEY (appointmentid);
ALTER TABLE  sqlex.asst_referee_mast
    ADD CONSTRAINT asst_referee_mast_pkey PRIMARY KEY (ass_ref_id);
ALTER TABLE  sqlex.block
    ADD CONSTRAINT block_pkey PRIMARY KEY (blockfloor, blockcode);
ALTER TABLE  sqlex.casino
    ADD CONSTRAINT casino_pkey PRIMARY KEY (casino);
ALTER TABLE  sqlex.coach_mast
    ADD CONSTRAINT coach_mast_pkey PRIMARY KEY (coach_id);
ALTER TABLE  sqlex.company_mast
    ADD CONSTRAINT company_mast_pkey PRIMARY KEY (com_id);
ALTER TABLE  sqlex.customer
    ADD CONSTRAINT customer_pkey PRIMARY KEY (customer_id);
ALTER TABLE  sqlex.department
    ADD CONSTRAINT department_pkey PRIMARY KEY (departmentid);
ALTER TABLE  sqlex.departments
    ADD CONSTRAINT departments_pkey PRIMARY KEY (department_id);
ALTER TABLE  sqlex.director
    ADD CONSTRAINT director_pkey PRIMARY KEY (dir_id);
ALTER TABLE  sqlex.elephants
    ADD CONSTRAINT elephants_pkey PRIMARY KEY (id);
ALTER TABLE  sqlex.emp_department
    ADD CONSTRAINT emp_department_pkey PRIMARY KEY (dpt_code);
ALTER TABLE  sqlex.emp_details
    ADD CONSTRAINT emp_details_pkey PRIMARY KEY (emp_idno);
ALTER TABLE  sqlex.emp
    ADD CONSTRAINT emp_pk PRIMARY KEY (eid);
ALTER TABLE  sqlex.employees
    ADD CONSTRAINT employees_pkey PRIMARY KEY (employee_id);
ALTER TABLE  sqlex.game_scores
    ADD CONSTRAINT game_scores_pkey PRIMARY KEY (id);
ALTER TABLE  sqlex.genres
    ADD CONSTRAINT genres_pkey PRIMARY KEY (gen_id);
ALTER TABLE  sqlex.goal_details
    ADD CONSTRAINT goal_details_pkey PRIMARY KEY (goal_id);
ALTER TABLE  sqlex.grades
    ADD CONSTRAINT grades_pkey PRIMARY KEY (id);
ALTER TABLE  sqlex.item_mast
    ADD CONSTRAINT item_mast_pkey PRIMARY KEY (pro_id);
ALTER TABLE  sqlex.job_grades
    ADD CONSTRAINT job_grades_pkey PRIMARY KEY (grade_level);
ALTER TABLE  sqlex.job_history
    ADD CONSTRAINT job_history_pkey PRIMARY KEY (employee_id, start_date);
ALTER TABLE  sqlex.jobs
    ADD CONSTRAINT jobs_pkey PRIMARY KEY (job_id);
ALTER TABLE  sqlex.locations
    ADD CONSTRAINT locations_pkey PRIMARY KEY (location_id);
ALTER TABLE  sqlex.manufacturers
    ADD CONSTRAINT manufacturers_pkey PRIMARY KEY (code);
ALTER TABLE  sqlex.match_mast
    ADD CONSTRAINT match_mast_pkey PRIMARY KEY (match_no);
ALTER TABLE  sqlex.medication
    ADD CONSTRAINT medication_pkey PRIMARY KEY (code);
ALTER TABLE  sqlex.movie
    ADD CONSTRAINT movie_pkey PRIMARY KEY (mov_id);
ALTER TABLE  sqlex.mytest1
    ADD CONSTRAINT mytest1_ord_num_key UNIQUE (ord_num);
ALTER TABLE  sqlex.mytest
    ADD CONSTRAINT mytest_ord_num_key UNIQUE (ord_num);
ALTER TABLE  sqlex.nurse
    ADD CONSTRAINT nurse_pkey PRIMARY KEY (employeeid);
ALTER TABLE  sqlex.on_call
    ADD CONSTRAINT on_call_pkey PRIMARY KEY (nurse, blockfloor, blockcode, oncallstart, oncallend);
ALTER TABLE  sqlex.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (ord_no);
ALTER TABLE  sqlex.partest1
    ADD CONSTRAINT partest1_ord_num_key UNIQUE (ord_num);
ALTER TABLE  sqlex.patient
    ADD CONSTRAINT patient_pkey PRIMARY KEY (ssn);
ALTER TABLE  sqlex.penalty_shootout
    ADD CONSTRAINT penalty_shootout_pkey PRIMARY KEY (kick_id);
ALTER TABLE  sqlex.physician
    ADD CONSTRAINT physician_pkey PRIMARY KEY (employeeid);
ALTER TABLE  sqlex.player_mast
    ADD CONSTRAINT player_mast_pkey PRIMARY KEY (player_id);
ALTER TABLE  sqlex.playing_position
    ADD CONSTRAINT playing_position_pkey PRIMARY KEY (position_id);
ALTER TABLE  sqlex.prescribes
    ADD CONSTRAINT prescribes_pkey PRIMARY KEY (physician, patient, medication, date);
ALTER TABLE  sqlex.procedure
    ADD CONSTRAINT procedure_pkey PRIMARY KEY (code);
ALTER TABLE  sqlex.referee_mast
    ADD CONSTRAINT referee_mast_pkey PRIMARY KEY (referee_id);
ALTER TABLE  sqlex.regions
    ADD CONSTRAINT regions_pkey PRIMARY KEY (region_id);
ALTER TABLE  sqlex.reviewer
    ADD CONSTRAINT reviewer_pkey PRIMARY KEY (rev_id);
ALTER TABLE  sqlex.room
    ADD CONSTRAINT room_pkey PRIMARY KEY (roomnumber);
ALTER TABLE  sqlex.salesman
    ADD CONSTRAINT salesman_pkey PRIMARY KEY (salesman_id);
ALTER TABLE  sqlex.scores
    ADD CONSTRAINT scores_pkey PRIMARY KEY (id);
ALTER TABLE  sqlex.soccer_city
    ADD CONSTRAINT soccer_city_pkey PRIMARY KEY (city_id);
ALTER TABLE  sqlex.soccer_country
    ADD CONSTRAINT soccer_country_pkey PRIMARY KEY (country_id);
ALTER TABLE  sqlex.soccer_venue
    ADD CONSTRAINT soccer_venue_pkey PRIMARY KEY (venue_id);
ALTER TABLE  sqlex.stay
    ADD CONSTRAINT stay_pkey PRIMARY KEY (stayid);
ALTER TABLE  sqlex.sybba
    ADD CONSTRAINT sybba_ord_num_key UNIQUE (ord_num);
ALTER TABLE  sqlex.trained_in
    ADD CONSTRAINT trained_in_pkey PRIMARY KEY (physician, treatment);
ALTER TABLE  sqlex.undergoes
    ADD CONSTRAINT undergoes_pkey PRIMARY KEY (patient, procedure, stay, date);
ALTER TABLE  sqlex.zebras
    ADD CONSTRAINT zebras_pkey PRIMARY KEY (id);
ALTER TABLE  sqlex.match_details
    ADD CONSTRAINT ass_ref_fkey FOREIGN KEY (ass_ref) REFERENCES sqlex.asst_referee_mast(ass_ref_id);
ALTER TABLE  sqlex.soccer_venue
    ADD CONSTRAINT city_id_fkey FOREIGN KEY (city_id) REFERENCES sqlex.soccer_city(city_id);
ALTER TABLE  sqlex.team_coaches
    ADD CONSTRAINT coach_id_fkey FOREIGN KEY (coach_id) REFERENCES sqlex.coach_mast(coach_id);
ALTER TABLE  sqlex.soccer_city
    ADD CONSTRAINT country_id_fkey FOREIGN KEY (country_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.referee_mast
    ADD CONSTRAINT country_id_fkey FOREIGN KEY (country_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.asst_referee_mast
    ADD CONSTRAINT country_id_fkey FOREIGN KEY (country_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.orders
    ADD CONSTRAINT customer_id_fk FOREIGN KEY (customer_id) REFERENCES sqlex.customer(customer_id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE  sqlex.emp_details
    ADD CONSTRAINT emp_details_emp_dept_fkey FOREIGN KEY (emp_dept) REFERENCES sqlex.emp_department(dpt_code);
ALTER TABLE  sqlex.prescribes
    ADD CONSTRAINT fk_appointment_appointmentid FOREIGN KEY (appointment) REFERENCES sqlex.appointment(appointmentid);
ALTER TABLE  sqlex.affiliated_with
    ADD CONSTRAINT fk_department_departmentid FOREIGN KEY (department) REFERENCES sqlex.department(departmentid);
ALTER TABLE  sqlex.prescribes
    ADD CONSTRAINT fk_medication_code FOREIGN KEY (medication) REFERENCES sqlex.medication(code);
ALTER TABLE  sqlex.appointment
    ADD CONSTRAINT fk_nurse_employeeid FOREIGN KEY (prepnurse) REFERENCES sqlex.nurse(employeeid);
ALTER TABLE  sqlex.undergoes
    ADD CONSTRAINT fk_nurse_employeeid FOREIGN KEY (assistingnurse) REFERENCES sqlex.nurse(employeeid);
ALTER TABLE  sqlex.on_call
    ADD CONSTRAINT fk_oncall_block_floor FOREIGN KEY (blockfloor, blockcode) REFERENCES sqlex.block(blockfloor, blockcode);
ALTER TABLE  sqlex.on_call
    ADD CONSTRAINT fk_oncall_nurse_employeeid FOREIGN KEY (nurse) REFERENCES sqlex.nurse(employeeid);
ALTER TABLE  sqlex.appointment
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES sqlex.patient(ssn);
ALTER TABLE  sqlex.prescribes
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES sqlex.patient(ssn);
ALTER TABLE  sqlex.stay
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES sqlex.patient(ssn);
ALTER TABLE  sqlex.undergoes
    ADD CONSTRAINT fk_patient_ssn FOREIGN KEY (patient) REFERENCES sqlex.patient(ssn);
ALTER TABLE  sqlex.department
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (head) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.affiliated_with
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.trained_in
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.patient
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (pcp) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.appointment
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.prescribes
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.undergoes
    ADD CONSTRAINT fk_physician_employeeid FOREIGN KEY (physician) REFERENCES sqlex.physician(employeeid);
ALTER TABLE  sqlex.trained_in
    ADD CONSTRAINT fk_procedure_code FOREIGN KEY (treatment) REFERENCES sqlex.procedure(code);
ALTER TABLE  sqlex.undergoes
    ADD CONSTRAINT fk_procedure_code FOREIGN KEY (procedure) REFERENCES sqlex.procedure(code);
ALTER TABLE  sqlex.room
    ADD CONSTRAINT fk_room_block_pk FOREIGN KEY (blockfloor, blockcode) REFERENCES sqlex.block(blockfloor, blockcode);
ALTER TABLE  sqlex.stay
    ADD CONSTRAINT fk_room_number FOREIGN KEY (room) REFERENCES sqlex.room(roomnumber);
ALTER TABLE  sqlex.undergoes
    ADD CONSTRAINT fk_stay_stayid FOREIGN KEY (stay) REFERENCES sqlex.stay(stayid);
ALTER TABLE  sqlex.item_mast
    ADD CONSTRAINT item_mast_pro_com_fkey FOREIGN KEY (pro_com) REFERENCES sqlex.company_mast(com_id);
ALTER TABLE  sqlex.match_details
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.player_booked
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.player_in_out
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.penalty_shootout
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.goal_details
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.match_captain
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.penalty_gk
    ADD CONSTRAINT match_no_fkey FOREIGN KEY (match_no) REFERENCES sqlex.match_mast(match_no);
ALTER TABLE  sqlex.movie_cast
    ADD CONSTRAINT movie_cast_act_id_fkey FOREIGN KEY (act_id) REFERENCES sqlex.actor(act_id);
ALTER TABLE  sqlex.movie_cast
    ADD CONSTRAINT movie_cast_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES sqlex.movie(mov_id);
ALTER TABLE  sqlex.movie_direction
    ADD CONSTRAINT movie_direction_dir_id_fkey FOREIGN KEY (dir_id) REFERENCES sqlex.director(dir_id);
ALTER TABLE  sqlex.movie_direction
    ADD CONSTRAINT movie_direction_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES sqlex.movie(mov_id);
ALTER TABLE  sqlex.movie_genres
    ADD CONSTRAINT movie_genres_gen_id_fkey FOREIGN KEY (gen_id) REFERENCES sqlex.genres(gen_id);
ALTER TABLE  sqlex.movie_genres
    ADD CONSTRAINT movie_genres_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES sqlex.movie(mov_id);
ALTER TABLE  sqlex.match_captain
    ADD CONSTRAINT player_captain_fkey FOREIGN KEY (player_captain) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.match_details
    ADD CONSTRAINT player_gk_fkey FOREIGN KEY (player_gk) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.penalty_gk
    ADD CONSTRAINT player_gk_fkey FOREIGN KEY (player_gk) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.player_booked
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.player_in_out
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.penalty_shootout
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.goal_details
    ADD CONSTRAINT player_id_fkey FOREIGN KEY (player_id) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.match_mast
    ADD CONSTRAINT plr_of_match_fkey FOREIGN KEY (plr_of_match) REFERENCES sqlex.player_mast(player_id);
ALTER TABLE  sqlex.player_mast
    ADD CONSTRAINT posi_to_play_fkey FOREIGN KEY (posi_to_play) REFERENCES sqlex.playing_position(position_id);
ALTER TABLE  sqlex.rating
    ADD CONSTRAINT rating_mov_id_fkey FOREIGN KEY (mov_id) REFERENCES sqlex.movie(mov_id);
ALTER TABLE  sqlex.rating
    ADD CONSTRAINT rating_rev_id_fkey FOREIGN KEY (rev_id) REFERENCES sqlex.reviewer(rev_id);
ALTER TABLE  sqlex.match_mast
    ADD CONSTRAINT referee_id_fkey FOREIGN KEY (referee_id) REFERENCES sqlex.referee_mast(referee_id);
ALTER TABLE  sqlex.customer
    ADD CONSTRAINT salesman_id_fk FOREIGN KEY (salesman_id) REFERENCES sqlex.salesman(salesman_id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE  sqlex.orders
    ADD CONSTRAINT salesman_id_fk2 FOREIGN KEY (salesman_id) REFERENCES sqlex.salesman(salesman_id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE  sqlex.team_coaches
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.player_mast
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.soccer_team
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.match_details
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.player_booked
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.player_in_out
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.penalty_shootout
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.goal_details
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.match_captain
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.penalty_gk
    ADD CONSTRAINT team_id_fkey FOREIGN KEY (team_id) REFERENCES sqlex.soccer_country(country_id);
ALTER TABLE  sqlex.match_mast
    ADD CONSTRAINT venue_id_fkey FOREIGN KEY (venue_id) REFERENCES sqlex.soccer_venue(venue_id);
