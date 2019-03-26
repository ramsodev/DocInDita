create schema empex;
--
-- Name: bonus; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE empex.bonus (
    emp_name character varying(15),
    job_name character varying(10),
    salary integer,
    commission integer
);



--
-- Name: department; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE empex.department (
    dep_id integer NOT NULL,
    dep_name character varying(20),
    dep_location character varying(15)
);




--
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE empex.employees (
    emp_id integer NOT NULL,
    emp_name character varying(15),
    job_name character varying(10),
    manager_id integer,
    hire_date date,
    salary numeric(10,2),
    commission numeric(7,2),
    dep_id integer
);




--
-- Name: salary_grade; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE empex.salary_grade (
    grade integer,
    min_sal integer,
    max_sal integer
);







--
-- Name: pk_dep_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  empex.department
    ADD CONSTRAINT pk_dep_id PRIMARY KEY (dep_id);


--
-- Name: pk_emp_id; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  empex.employees
    ADD CONSTRAINT pk_emp_id PRIMARY KEY (emp_id);


--
-- Name: fk_dep_id; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  empex.employees
    ADD CONSTRAINT fk_dep_id FOREIGN KEY (dep_id) REFERENCES empex.department(dep_id);

