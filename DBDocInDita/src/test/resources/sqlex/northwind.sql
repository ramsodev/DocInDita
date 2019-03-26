create schema northwind;
--
-- Name: categories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.categories (
    categoryid smallint NOT NULL,
    categoryname character varying(15) NOT NULL,
    description clob,
    picture blob
);



--
-- Name: customercustomerdemo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.customercustomerdemo (
    customerid char(50) NOT NULL,
    customertypeid char(50) NOT NULL
);




--
-- Name: customerdemographics; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.customerdemographics (
    customertypeid char(50) NOT NULL,
    customerdesc clob
);




--
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.customers (
    customerid char(50) NOT NULL,
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



--
-- Name: employees; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.employees (
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
    photo blob,
    notes clob,
    reportsto smallint,
    photopath character varying(255)
);




--
-- Name: employeeterritories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.employeeterritories (
    employeeid smallint NOT NULL,
    territoryid character varying(20) NOT NULL
);




--
-- Name: order_details; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.order_details (
    orderid smallint NOT NULL,
    productid smallint NOT NULL,
    unitprice real NOT NULL,
    quantity smallint NOT NULL,
    discount real NOT NULL
);




--
-- Name: orders; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.orders (
    orderid smallint NOT NULL,
    customerid char(50),
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




--
-- Name: products; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.products (
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




--
-- Name: region; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.region (
    regionid smallint NOT NULL,
    regiondescription char(50) NOT NULL
);



--
-- Name: shippers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.shippers (
    shipperid smallint NOT NULL,
    companyname character varying(40) NOT NULL,
    phone character varying(24)
);




--
-- Name: suppliers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.suppliers (
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
    homepage clob
);




--
-- Name: territories; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.territories (
    territoryid character varying(20) NOT NULL,
    territorydescription char(50) NOT NULL,
    regionid smallint NOT NULL
);




--
-- Name: usstates; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE northwind.usstates (
    stateid smallint NOT NULL,
    statename character varying(100),
    stateabbr character varying(2),
    stateregion character varying(50)
);

--
-- Name: pk_categories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.categories
    ADD CONSTRAINT pk_categories PRIMARY KEY (categoryid);


--
-- Name: pk_customercustomerdemo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.customercustomerdemo
    ADD CONSTRAINT pk_customercustomerdemo PRIMARY KEY (customerid, customertypeid);


--
-- Name: pk_customerdemographics; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.customerdemographics
    ADD CONSTRAINT pk_customerdemographics PRIMARY KEY (customertypeid);


--
-- Name: pk_customers; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.customers
    ADD CONSTRAINT pk_customers PRIMARY KEY (customerid);


--
-- Name: pk_employees; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.employees
    ADD CONSTRAINT pk_employees PRIMARY KEY (employeeid);


--
-- Name: pk_employeeterritories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.employeeterritories
    ADD CONSTRAINT pk_employeeterritories PRIMARY KEY (employeeid, territoryid);


--
-- Name: pk_order_details; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.order_details
    ADD CONSTRAINT pk_order_details PRIMARY KEY (orderid, productid);


--
-- Name: pk_orders; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.orders
    ADD CONSTRAINT pk_orders PRIMARY KEY (orderid);


--
-- Name: pk_products; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.products
    ADD CONSTRAINT pk_products PRIMARY KEY (productid);


--
-- Name: pk_region; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.region
    ADD CONSTRAINT pk_region PRIMARY KEY (regionid);


--
-- Name: pk_shippers; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.shippers
    ADD CONSTRAINT pk_shippers PRIMARY KEY (shipperid);


--
-- Name: pk_suppliers; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.suppliers
    ADD CONSTRAINT pk_suppliers PRIMARY KEY (supplierid);


--
-- Name: pk_territories; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.territories
    ADD CONSTRAINT pk_territories PRIMARY KEY (territoryid);


--
-- Name: fk_customercustomerdemo_customerdemographics; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.customercustomerdemo
    ADD CONSTRAINT fk_customercustomerdemo_customerdemographics FOREIGN KEY (customertypeid) REFERENCES northwind.customerdemographics(customertypeid);


--
-- Name: fk_customercustomerdemo_customers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.customercustomerdemo
    ADD CONSTRAINT fk_customercustomerdemo_customers FOREIGN KEY (customerid) REFERENCES northwind.customers(customerid);


--
-- Name: fk_employees_employees; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.employees
    ADD CONSTRAINT fk_employees_employees FOREIGN KEY (reportsto) REFERENCES northwind.employees(employeeid);


--
-- Name: fk_employeeterritories_employees; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.employeeterritories
    ADD CONSTRAINT fk_employeeterritories_employees FOREIGN KEY (employeeid) REFERENCES northwind.employees(employeeid);


--
-- Name: fk_employeeterritories_territories; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.employeeterritories
    ADD CONSTRAINT fk_employeeterritories_territories FOREIGN KEY (territoryid) REFERENCES northwind.territories(territoryid);


--
-- Name: fk_order_details_orders; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.order_details
    ADD CONSTRAINT fk_order_details_orders FOREIGN KEY (orderid) REFERENCES northwind.orders(orderid);


--
-- Name: fk_order_details_products; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.order_details
    ADD CONSTRAINT fk_order_details_products FOREIGN KEY (productid) REFERENCES northwind.products(productid);


--
-- Name: fk_orders_customers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.orders
    ADD CONSTRAINT fk_orders_customers FOREIGN KEY (customerid) REFERENCES northwind.customers(customerid);


--
-- Name: fk_orders_employees; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.orders
    ADD CONSTRAINT fk_orders_employees FOREIGN KEY (employeeid) REFERENCES northwind.employees(employeeid);


--
-- Name: fk_orders_shippers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.orders
    ADD CONSTRAINT fk_orders_shippers FOREIGN KEY (shipvia) REFERENCES northwind.shippers(shipperid);


--
-- Name: fk_products_categories; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.products
    ADD CONSTRAINT fk_products_categories FOREIGN KEY (categoryid) REFERENCES northwind.categories(categoryid);


--
-- Name: fk_products_suppliers; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.products
    ADD CONSTRAINT fk_products_suppliers FOREIGN KEY (supplierid) REFERENCES northwind.suppliers(supplierid);


--
-- Name: fk_territories_region; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE  northwind.territories
    ADD CONSTRAINT fk_territories_region FOREIGN KEY (regionid) REFERENCES northwind.region(regionid);




