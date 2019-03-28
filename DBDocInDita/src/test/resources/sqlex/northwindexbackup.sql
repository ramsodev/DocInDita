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
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categories (
    categoryid smallint NOT NULL,
    categoryname character varying(15) NOT NULL,
    description text,
    picture bytea
);


ALTER TABLE public.categories OWNER TO postgres;

--
-- Name: customercustomerdemo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customercustomerdemo (
    customerid bpchar NOT NULL,
    customertypeid bpchar NOT NULL
);


ALTER TABLE public.customercustomerdemo OWNER TO postgres;

--
-- Name: customerdemographics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customerdemographics (
    customertypeid bpchar NOT NULL,
    customerdesc text
);


ALTER TABLE public.customerdemographics OWNER TO postgres;

--
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    customerid bpchar NOT NULL,
    companyname character varying(40) NOT NULL,
    contactname character varying(30),
    contacttitle character varying(30),
    address character varying(60),
    city character varying(15),
    region character varying(15),
    postalcode character varying(10),
    country character varying(15),
    phone character varying(24),
    fax character varying(24)
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employees (
    employeeid smallint NOT NULL,
    lastname character varying(20) NOT NULL,
    firstname character varying(10) NOT NULL,
    title character varying(30),
    titleofcourtesy character varying(25),
    birthdate date,
    hiredate date,
    address character varying(60),
    city character varying(15),
    region character varying(15),
    postalcode character varying(10),
    country character varying(15),
    homephone character varying(24),
    extension character varying(4),
    photo bytea,
    notes text,
    reportsto smallint,
    photopath character varying(255)
);


ALTER TABLE public.employees OWNER TO postgres;

--
-- Name: employeeterritories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.employeeterritories (
    employeeid smallint NOT NULL,
    territoryid character varying(20) NOT NULL
);


ALTER TABLE public.employeeterritories OWNER TO postgres;

--
-- Name: order_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.order_details (
    orderid smallint NOT NULL,
    productid smallint NOT NULL,
    unitprice real NOT NULL,
    quantity smallint NOT NULL,
    discount real NOT NULL
);


ALTER TABLE public.order_details OWNER TO postgres;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.orders (
    orderid smallint NOT NULL,
    customerid bpchar,
    employeeid smallint,
    orderdate date,
    requireddate date,
    shippeddate date,
    shipvia smallint,
    freight real,
    shipname character varying(40),
    shipaddress character varying(60),
    shipcity character varying(15),
    shipregion character varying(15),
    shippostalcode character varying(10),
    shipcountry character varying(15)
);


ALTER TABLE public.orders OWNER TO postgres;

--
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.products (
    productid smallint NOT NULL,
    productname character varying(40) NOT NULL,
    supplierid smallint,
    categoryid smallint,
    quantityperunit character varying(20),
    unitprice real,
    unitsinstock smallint,
    unitsonorder smallint,
    reorderlevel smallint,
    discontinued integer NOT NULL
);


ALTER TABLE public.products OWNER TO postgres;

--
-- Name: region; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.region (
    regionid smallint NOT NULL,
    regiondescription bpchar NOT NULL
);


ALTER TABLE public.region OWNER TO postgres;

--
-- Name: shippers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.shippers (
    shipperid smallint NOT NULL,
    companyname character varying(40) NOT NULL,
    phone character varying(24)
);


ALTER TABLE public.shippers OWNER TO postgres;

--
-- Name: suppliers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.suppliers (
    supplierid smallint NOT NULL,
    companyname character varying(40) NOT NULL,
    contactname character varying(30),
    contacttitle character varying(30),
    address character varying(60),
    city character varying(15),
    region character varying(15),
    postalcode character varying(10),
    country character varying(15),
    phone character varying(24),
    fax character varying(24),
    homepage text
);


ALTER TABLE public.suppliers OWNER TO postgres;

--
-- Name: territories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.territories (
    territoryid character varying(20) NOT NULL,
    territorydescription bpchar NOT NULL,
    regionid smallint NOT NULL
);


ALTER TABLE public.territories OWNER TO postgres;

--
-- Name: usstates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usstates (
    stateid smallint NOT NULL,
    statename character varying(100),
    stateabbr character varying(2),
    stateregion character varying(50)
);


ALTER TABLE public.usstates OWNER TO postgres;

--
--
-- Name: pk_categories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT pk_categories PRIMARY KEY (categoryid);


--
-- Name: pk_customercustomerdemo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customercustomerdemo
    ADD CONSTRAINT pk_customercustomerdemo PRIMARY KEY (customerid, customertypeid);


--
-- Name: pk_customerdemographics; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customerdemographics
    ADD CONSTRAINT pk_customerdemographics PRIMARY KEY (customertypeid);


--
-- Name: pk_customers; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT pk_customers PRIMARY KEY (customerid);


--
-- Name: pk_employees; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT pk_employees PRIMARY KEY (employeeid);


--
-- Name: pk_employeeterritories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employeeterritories
    ADD CONSTRAINT pk_employeeterritories PRIMARY KEY (employeeid, territoryid);


--
-- Name: pk_order_details; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT pk_order_details PRIMARY KEY (orderid, productid);


--
-- Name: pk_orders; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT pk_orders PRIMARY KEY (orderid);


--
-- Name: pk_products; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT pk_products PRIMARY KEY (productid);


--
-- Name: pk_region; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.region
    ADD CONSTRAINT pk_region PRIMARY KEY (regionid);


--
-- Name: pk_shippers; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.shippers
    ADD CONSTRAINT pk_shippers PRIMARY KEY (shipperid);


--
-- Name: pk_suppliers; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.suppliers
    ADD CONSTRAINT pk_suppliers PRIMARY KEY (supplierid);


--
-- Name: pk_territories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.territories
    ADD CONSTRAINT pk_territories PRIMARY KEY (territoryid);


--
-- Name: fk_customercustomerdemo_customerdemographics; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customercustomerdemo
    ADD CONSTRAINT fk_customercustomerdemo_customerdemographics FOREIGN KEY (customertypeid) REFERENCES public.customerdemographics(customertypeid);


--
-- Name: fk_customercustomerdemo_customers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customercustomerdemo
    ADD CONSTRAINT fk_customercustomerdemo_customers FOREIGN KEY (customerid) REFERENCES public.customers(customerid);


--
-- Name: fk_employees_employees; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employees
    ADD CONSTRAINT fk_employees_employees FOREIGN KEY (reportsto) REFERENCES public.employees(employeeid);


--
-- Name: fk_employeeterritories_employees; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employeeterritories
    ADD CONSTRAINT fk_employeeterritories_employees FOREIGN KEY (employeeid) REFERENCES public.employees(employeeid);


--
-- Name: fk_employeeterritories_territories; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.employeeterritories
    ADD CONSTRAINT fk_employeeterritories_territories FOREIGN KEY (territoryid) REFERENCES public.territories(territoryid);


--
-- Name: fk_order_details_orders; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT fk_order_details_orders FOREIGN KEY (orderid) REFERENCES public.orders(orderid);


--
-- Name: fk_order_details_products; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.order_details
    ADD CONSTRAINT fk_order_details_products FOREIGN KEY (productid) REFERENCES public.products(productid);


--
-- Name: fk_orders_customers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk_orders_customers FOREIGN KEY (customerid) REFERENCES public.customers(customerid);


--
-- Name: fk_orders_employees; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk_orders_employees FOREIGN KEY (employeeid) REFERENCES public.employees(employeeid);


--
-- Name: fk_orders_shippers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk_orders_shippers FOREIGN KEY (shipvia) REFERENCES public.shippers(shipperid);


--
-- Name: fk_products_categories; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_categories FOREIGN KEY (categoryid) REFERENCES public.categories(categoryid);


--
-- Name: fk_products_suppliers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.products
    ADD CONSTRAINT fk_products_suppliers FOREIGN KEY (supplierid) REFERENCES public.suppliers(supplierid);


--
-- Name: fk_territories_region; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.territories
    ADD CONSTRAINT fk_territories_region FOREIGN KEY (regionid) REFERENCES public.region(regionid);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- Name: TABLE categories; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.categories FROM PUBLIC;
REVOKE ALL ON TABLE public.categories FROM postgres;
GRANT ALL ON TABLE public.categories TO postgres;
GRANT SELECT ON TABLE public.categories TO postgres;


--
-- Name: TABLE customercustomerdemo; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.customercustomerdemo FROM PUBLIC;
REVOKE ALL ON TABLE public.customercustomerdemo FROM postgres;
GRANT ALL ON TABLE public.customercustomerdemo TO postgres;
GRANT SELECT ON TABLE public.customercustomerdemo TO postgres;


--
-- Name: TABLE customerdemographics; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.customerdemographics FROM PUBLIC;
REVOKE ALL ON TABLE public.customerdemographics FROM postgres;
GRANT ALL ON TABLE public.customerdemographics TO postgres;
GRANT SELECT ON TABLE public.customerdemographics TO postgres;


--
-- Name: TABLE customers; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.customers FROM PUBLIC;
REVOKE ALL ON TABLE public.customers FROM postgres;
GRANT ALL ON TABLE public.customers TO postgres;
GRANT SELECT ON TABLE public.customers TO postgres;


--
-- Name: TABLE employees; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.employees FROM PUBLIC;
REVOKE ALL ON TABLE public.employees FROM postgres;
GRANT ALL ON TABLE public.employees TO postgres;
GRANT SELECT ON TABLE public.employees TO postgres;


--
-- Name: TABLE employeeterritories; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.employeeterritories FROM PUBLIC;
REVOKE ALL ON TABLE public.employeeterritories FROM postgres;
GRANT ALL ON TABLE public.employeeterritories TO postgres;
GRANT SELECT ON TABLE public.employeeterritories TO postgres;


--
-- Name: TABLE order_details; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.order_details FROM PUBLIC;
REVOKE ALL ON TABLE public.order_details FROM postgres;
GRANT ALL ON TABLE public.order_details TO postgres;
GRANT SELECT ON TABLE public.order_details TO postgres;


--
-- Name: TABLE orders; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.orders FROM PUBLIC;
REVOKE ALL ON TABLE public.orders FROM postgres;
GRANT ALL ON TABLE public.orders TO postgres;
GRANT SELECT ON TABLE public.orders TO postgres;


--
-- Name: TABLE products; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.products FROM PUBLIC;
REVOKE ALL ON TABLE public.products FROM postgres;
GRANT ALL ON TABLE public.products TO postgres;
GRANT SELECT ON TABLE public.products TO postgres;


--
-- Name: TABLE region; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.region FROM PUBLIC;
REVOKE ALL ON TABLE public.region FROM postgres;
GRANT ALL ON TABLE public.region TO postgres;
GRANT SELECT ON TABLE public.region TO postgres;


--
-- Name: TABLE shippers; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.shippers FROM PUBLIC;
REVOKE ALL ON TABLE public.shippers FROM postgres;
GRANT ALL ON TABLE public.shippers TO postgres;
GRANT SELECT ON TABLE public.shippers TO postgres;


--
-- Name: TABLE suppliers; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.suppliers FROM PUBLIC;
REVOKE ALL ON TABLE public.suppliers FROM postgres;
GRANT ALL ON TABLE public.suppliers TO postgres;
GRANT SELECT ON TABLE public.suppliers TO postgres;


--
-- Name: TABLE territories; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.territories FROM PUBLIC;
REVOKE ALL ON TABLE public.territories FROM postgres;
GRANT ALL ON TABLE public.territories TO postgres;
GRANT SELECT ON TABLE public.territories TO postgres;


--
-- Name: TABLE usstates; Type: ACL; Schema: public; Owner: postgres
--

REVOKE ALL ON TABLE public.usstates FROM PUBLIC;
REVOKE ALL ON TABLE public.usstates FROM postgres;
GRANT ALL ON TABLE public.usstates TO postgres;
GRANT SELECT ON TABLE public.usstates TO postgres;


--
-- PostgreSQL database dump complete
--

