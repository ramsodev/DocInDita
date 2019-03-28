--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.16
-- Dumped by pg_dump version 9.5.12

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: bonus; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bonus (
    emp_name character varying(15),
    job_name character varying(10),
    salary integer,
    commission integer
);


ALTER TABLE public.bonus OWNER TO postgres;

--
-- Name: department; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.department (
    dep_id integer NOT NULL,
    dep_name character varying(20),
    dep_location character varying(15)
);


ALTER TABLE public.department OWNER TO postgres;

--
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    emp_id integer NOT NULL,
    emp_name character varying(15),
    job_name character varying(10),
    manager_id integer,
    hire_date date,
    salary numeric(10,2),
    commission numeric(7,2),
    dep_id integer
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- Name: salary_grade; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.salary_grade (
    grade integer,
    min_sal integer,
    max_sal integer
);


ALTER TABLE public.salary_grade OWNER TO postgres;



--
-- Name: pk_dep_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.department
    ADD CONSTRAINT pk_dep_id PRIMARY KEY (dep_id);


--
-- Name: pk_emp_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT pk_emp_id PRIMARY KEY (emp_id);


--
-- Name: fk_dep_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT fk_dep_id FOREIGN KEY (dep_id) REFERENCES public.department(dep_id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: TABLE bonus; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.bonus FROM PUBLIC;
REVOKE ALL ON TABLE public.bonus FROM postgres;
GRANT ALL ON TABLE public.bonus TO postgres;
GRANT SELECT ON TABLE public.bonus TO postgres;


--
-- Name: TABLE department; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.department FROM PUBLIC;
REVOKE ALL ON TABLE public.department FROM postgres;
GRANT ALL ON TABLE public.department TO postgres;
GRANT SELECT ON TABLE public.department TO postgres;


--
-- Name: TABLE employees; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.employees FROM PUBLIC;
REVOKE ALL ON TABLE public.employees FROM postgres;
GRANT ALL ON TABLE public.employees TO postgres;
GRANT SELECT ON TABLE public.employees TO postgres;


--
-- Name: TABLE salary_grade; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.salary_grade FROM PUBLIC;
REVOKE ALL ON TABLE public.salary_grade FROM postgres;
GRANT ALL ON TABLE public.salary_grade TO postgres;
GRANT SELECT ON TABLE public.salary_grade TO postgres;


--
-- PostgreSQL database dump complete
--

