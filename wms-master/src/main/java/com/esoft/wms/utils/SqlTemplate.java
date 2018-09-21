package com.esoft.wms.utils;

public interface SqlTemplate {
	/** 通过taskID 查询taskInfo 表的task_param */
	static final String FIND_TASKINFO_PARAM_BY_SQL = "FROM TaskInfo T WHERE T.id = ";

	// 更新jobId 到相应记录
	static final String UPDATE_JOBID = "UPDATE TB_DAE_TARSK_INSTANCE SET JOB_ID = '%s' WHERE ID = %s";
	// 更新PROJECT 的contextName
	static final String UPDATE_PROJECT_CONTEXTNAME = "UPDATE TB_DAE_PROJECT_INFO SET CONTEXT_NAME = \"%s\" WHERE id = %s";
	// 通过用户查询可用的资源
	static final String QUERY_USER_RES = "SELECT m.DRIVE_RAM,m.EXECUTOR_RAM,m.CORE_NUMBER FROM TB_DAE_MECHANISM_INFO m,TB_DAE_MECHANISM_USER_REF mf"
			+ " WHERE m.MECHANISM_ID = mf.MECHANISM_ID AND mf.USER_CODE IN (SELECT LINK_USER FROM TB_DAE_PROJECT_INFO  WHERE ID = %s)";
	// 查询project 对应的context
	static final String QUERY_PROJECT_CONTEXT = "SELECT CONTEXT_NAME FROM TB_DAE_PROJECT_INFO WHERE ID = %s";
	static final String FIND_TASK_RETURN = "SELECT t.PROJECT_ID,t.ID,j.ITEM_UUID,t.INS_STATUS,t.JOB_UUID,j.SERIAL_ID FROM"
			+ " TB_DAE_TARSK_INSTANCE as t INNER JOIN TB_DAE_JOB as j ON t.JOB_UUID = j.JOB_UUID"
			+ " WHERE j.SCAN_NUM = 0 AND t.INS_STATUS in ('"
			+ ConstanceInfo.TASK_INSTANCE_STATUS_HIVED
			+ "','"
			+ ConstanceInfo.TASK_INSTANCE_STATUS_SPARKFAILED
			+ "','"
			+ ConstanceInfo.TASK_INSTANCE_STATUS_HIVEFAL + "') ";
	// 对project的操作
	static final String UPDATE_PROJECT_LINK = "UPDATE TB_DAE_PROJECT_INFO SET LINK_USER = %s,LINK_UUID = \"%s\" WHERE ID = %s";
	static final String UPDATE_PROJECT_REMAIN_JOB = "UPDATE TB_DAE_PROJECT_INFO SET REMAIN_JOB = %s WHERE ID = %s";
	static final String INIT_PROJECT = "UPDATE TB_DAE_PROJECT_INFO SET PROJECT_STATUS = 'start',REMAIN_JOB = %s,HAS_FAIL_JOB = 0 WHERE ID = %s";
	static final String FLAG_PROJECT_FAIL = "UPDATE TB_DAE_PROJECT_INFO SET REMAIN_JOB = %s,HAS_FAIL_JOB = 1 WHERE ID = %s";
	static final String QUERY_PROJECT_CONDITION = "SELECT REMAIN_JOB,HAS_FAIL_JOB FROM TB_DAE_PROJECT_INFO WHERE ID = %s";
	static final String UPDATE_PROJECT_STATUS = "UPDATE TB_DAE_PROJECT_INFO SET PROJECT_STATUS = '%s' WHERE ID = %s";
	static final String UPDATE_PROJECT_OP = "UPDATE TB_DAE_PROJECT_INFO SET OPRATING = %s WHERE ID = %s";

	static final String QUERY_CHILDREN = "SELECT SON_ITEM_UUID FROM TB_DAE_JOB WHERE JOB_UUID = \"%s\"";
	static final String UPDATE_JOB_SCAN_NUM = "UPDATE TB_DAE_JOB SET SCAN_NUM = 1 WHERE JOB_UUID = \"%s\"";
	static final String QUERY_EXE_CONDITION = "SELECT PARENT_NUM,END_PARENT_NUM,TASK_INS_JSON,JOB_UUID FROM TB_DAE_JOB WHERE SERIAL_ID = '%s' AND ITEM_UUID = '%s' ";
	static final String UPDATE_END_PARENT_NUM = "UPDATE TB_DAE_JOB SET END_PARENT_NUM = END_PARENT_NUM+1 WHERE JOB_UUID = \"%s\"";
	static final String FIND_REMAIN_JOBS = "SELECT ITEM_UUID,SON_ITEM_UUID,JOB_UUID FROM TB_DAE_JOB WHERE SERIAL_ID = '%s' AND SCAN_NUM = 0";
}