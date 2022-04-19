/**
 * for GenericSQLOperation;
 * how to build a SQL query string with a GenericSQLQuery object;
 */

/*********************************************/
select * from APP_VF_DATA.MONO1_NODE;

/**
 * test
 */
select min(RUID),  sibling_index, is_leaf from APP_VF_DATA.MONO1_NODE group by sibling_index, is_leaf;


/************************************************/
/*use subquery instead of table view*/
/*add row number to the SELECT clause of user-defined sql query: https://db.apache.org/derby/docs/10.4/ref/rreffuncrownumber.html*/
/*the query will be run twice????? thus not efficient?????*/
/*also if the row number generation is not the consistent for t1 and t2, this method will not work*/
/*see the table view method below, which does not have this issue!*/
select c1, c2, c3, c4, c5 /*select the set of columns to be put in output data table schema*/
from 
	(select ROW_NUMBER() over () as rn, RUID c1, NODE_ID c2, parent_id c3, is_leaf c4, sibling_index c5 from APP_VF_DATA.MONO1_NODE where parent_id>3) t1 /*t1 and t2 are exactly the same and are both based on the user defined SQL query*/
where t1.rn in
	(
		select min(t2.rn)
		from (select ROW_NUMBER() over () as rn, RUID c1, NODE_ID c2, parent_id c3, is_leaf c4, sibling_index c5 from APP_VF_DATA.MONO1_NODE where parent_id>3) t2 /*t1 and t2 are exactly the same and are both based on the user defined SQL query*/
		where t2.c4 is not null and t2.c5 is not null /*primary key columns not null*/
		group by t2.c4, t2.c5 /*group by primary key columns*/
	);

	
/*remove the table name from column names-still working*/
/*it seems that the table alias t1 and t2 cannot be removed?*/
select c1, c2, c3, c4, c5 /*select the set of columns to be put in output data table schema*/
from 
	(select ROW_NUMBER() over () as rn, RUID c1, NODE_ID c2, parent_id c3, is_leaf c4, sibling_index c5 from APP_VF_DATA.MONO1_NODE where parent_id>3) t1/*t1 and t2 are exactly the same and are both based on the user defined SQL query*/
where rn in
	(
		select min(rn)
		from (select ROW_NUMBER() over () as rn, RUID c1, NODE_ID c2, parent_id c3, is_leaf c4, sibling_index c5 from APP_VF_DATA.MONO1_NODE where parent_id>3) t2/*t1 and t2 are exactly the same and are both based on the user defined SQL query*/
		where c4 is not null and c5 is not null /*primary key columns not null*/
		group by c4, c5 /*group by primary key columns*/
	);

/***************************************************/
/*use table view instead of subquery*/
/*first create a table view with a modified user-defined sql query: add row number column and add column alias to the user defined sql query*/
create view t1 as  
	select ROW_NUMBER() over () as rn, RUID c1, NODE_ID c2, parent_id c3, is_leaf c4, sibling_index c5 from APP_VF_DATA.MONO1_NODE where parent_id>3; /*this is based on the customized SQL query*/

/* */
select c1, c2, c3, c4, c5 from t1 /*select the columns that are to be put in output data table schema*/
where t1.rn in /*c1 is one of the non primary key columns*/
	(
		select min(rn)
		from t1
		where c4 is not null and c5 is not null /*every primary key column not null*/
		group by c4, c5
	);

/*drop table view*/
drop view t1;


/**********************************************************/
/*test insert into output data table with RUID column auto-populated and with the result of a SELECT-FROM query*/
/*first create a output table with RUID column auto-generated*/
create table APP_VF_DATA.t1
(
	RUID int GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
	node_id int not null,
	leaf_index double,
	PRIMARY KEY(node_id)
);

/*insert into the output table without explicitly selecting the RUID column of the input data table*/
insert into APP_VF_DATA.t1 (node_id, leaf_index)
select node_id,leaf_index from APP_VF_DATA.MONO50_NODE;

/*check if worked - YES*/
select * from APP_VF_DATA.t1;




/**/