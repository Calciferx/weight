create table dbo.dtproperties
(
    id       int identity(10, 1),
    objectid int,
    property varchar(64)     not null,
    value    varchar(255),
    uvalue   nvarchar(255),
    lvalue   image,
    version  int default (0) not null,
    constraint pk_dtproperties
        primary key nonclustered (id, property)
)
go

create table dbo.hqb_modifylog
(
    WeightGuid    nvarchar(36),
    ID            int not null
        constraint pk_hqb_modifylog_id
            primary key nonclustered,
    company_code  nvarchar(50),
    LSH           nvarchar(100),
    CarID         nvarchar(50),
    WeighType     nvarchar(5),
    SendDW        nvarchar(50),
    ReceiveDW     nvarchar(50),
    CargoName     nvarchar(50),
    StandardOf    nvarchar(50),
    GrossWeight   decimal(18, 3) default (0),
    ActualTare    decimal(18, 3) default (0),
    NetWeight     decimal(18, 3) default (0),
    DecWeight     decimal(18, 3) default (0),
    TrueWeight    decimal(18, 3) default (0),
    UnitPrice     decimal(18, 3) default (0),
    TheMoney      decimal(18, 3) default (0),
    zfxs          decimal(18, 3) default (0),
    fl            decimal(18, 3) default (0),
    WeighingFee   decimal(18, 3) default (0),
    MzWeighman    nvarchar(50),
    PzWeighman    nvarchar(50),
    MzStation     nvarchar(10),
    PzStation     nvarchar(10),
    MzTime        datetime,
    PzTime        datetime,
    Weigh1Time    datetime,
    Weigh2Time    datetime,
    UpdateMan     nvarchar(50),
    UpdateTime    datetime,
    Memo          nvarchar(50),
    PrintTimes    int            default (0),
    IsUpdated     bit,
    Spare1        nvarchar(50),
    Spare2        nvarchar(50),
    Spare3        nvarchar(50),
    Spare4        nvarchar(50),
    Spare5        nvarchar(50),
    Spare6        decimal(18, 3) default (0),
    Spare7        decimal(18, 3) default (0),
    Spare8        decimal(18, 3) default (0),
    Spare9        decimal(18, 3) default (0),
    Spare10       nvarchar(50),
    Spare11       nvarchar(50),
    Spare12       nvarchar(50),
    Spare13       nvarchar(50),
    Spare14       nvarchar(50),
    Spare15       decimal(18, 3) default (0),
    Spare16       decimal(18, 3) default (0),
    Spare17       decimal(18, 3) default (0),
    Spare18       decimal(18, 3) default (0),
    Spare19       nvarchar(50),
    Spare20       nvarchar(50),
    Spare21       nvarchar(50),
    Spare22       nvarchar(50),
    Spare23       nvarchar(50),
    Spare24       nvarchar(50),
    Spare25       nvarchar(50),
    Spare26       nvarchar(50),
    Spare27       nvarchar(50),
    Spare28       nvarchar(50),
    modify_onnet  varchar,
    modify_time   datetime,
    modify_by     nvarchar(50),
    download_time datetime,
    syn_flag      varchar
)
go

create table dbo.net_price
(
    id           nvarchar(25)               not null
        constraint net_price_id
            primary key nonclustered,
    company_code nvarchar(50),
    cargo_name   nvarchar(50),
    cargo_price  decimal(18, 3) default (0) not null,
    cargo_code   nvarchar(50),
    update_time  datetime,
    pym          nvarchar(50),
    flag_enabled int            default (1) not null,
    update_by    nvarchar(20),
    b0           int            default (1) not null
)
go

create table dbo.pbcatcol
(
    pbc_tnam char(30),
    pbc_tid  int,
    pbc_ownr char(30),
    pbc_cnam char(30),
    pbc_cid  smallint,
    pbc_labl varchar(254),
    pbc_lpos smallint,
    pbc_hdr  varchar(254),
    pbc_hpos smallint,
    pbc_jtfy smallint,
    pbc_mask varchar(31),
    pbc_case smallint,
    pbc_hght smallint,
    pbc_wdth smallint,
    pbc_ptrn varchar(31),
    pbc_bmap char,
    pbc_init varchar(254),
    pbc_cmnt varchar(254),
    pbc_edit varchar(31),
    pbc_tag  varchar(254),
    constraint pbcatcol_idx
        unique (pbc_tnam, pbc_ownr, pbc_cnam)
)
go

create table dbo.pbcatedt
(
    pbe_name varchar(30)     not null,
    pbe_edit varchar(254),
    pbe_type smallint not null,
    pbe_cntr int,
    pbe_seqn smallint not null,
    pbe_flag int,
    pbe_work char(32),
    constraint pbcatedt_idx
        unique (pbe_name, pbe_seqn)
)
go

create table dbo.pbcatfmt
(
    pbf_name varchar(30)     not null
        constraint pbcatfmt_idx
            unique,
    pbf_frmt varchar(254)    not null,
    pbf_type smallint not null,
    pbf_cntr int
)
go

create table dbo.pbcattbl
(
    pbt_tnam char(30),
    pbt_tid  int,
    pbt_ownr char(30),
    pbd_fhgt smallint,
    pbd_fwgt smallint,
    pbd_fitl char,
    pbd_funl char,
    pbd_fchr smallint,
    pbd_fptc smallint,
    pbd_ffce char(32),
    pbh_fhgt smallint,
    pbh_fwgt smallint,
    pbh_fitl char,
    pbh_funl char,
    pbh_fchr smallint,
    pbh_fptc smallint,
    pbh_ffce char(32),
    pbl_fhgt smallint,
    pbl_fwgt smallint,
    pbl_fitl char,
    pbl_funl char,
    pbl_fchr smallint,
    pbl_fptc smallint,
    pbl_ffce char(32),
    pbt_cmnt varchar(254),
    constraint pbcattbl_idx
        unique (pbt_tnam, pbt_ownr)
)
go

create table dbo.pbcatvld
(
    pbv_name varchar(30)     not null
        constraint pbcatvld_idx
            unique,
    pbv_vald varchar(254)    not null,
    pbv_type smallint not null,
    pbv_cntr int,
    pbv_msg  varchar(254)
)
go

create table dbo.sys_button
(
    id      nvarchar(254),
    name    nvarchar(254),
    sort    int,
    code    nvarchar(254),
    menu_id nvarchar(254),
    type    int,
    url     nvarchar(254),
    remark  nvarchar(254),
    e_name  nvarchar(254)
)
go

create table dbo.sys_language
(
    id          nvarchar(254),
    code        nvarchar(254),
    e_name      nvarchar(254),
    c_name      nvarchar(254),
    status      nvarchar(254),
    remark      nvarchar(254),
    create_time nvarchar(254)
)
go

create table dbo.sys_log_info
(
    id            nvarchar(254),
    type          nvarchar(254),
    modular       nvarchar(254),
    function_log  nvarchar(254),
    operation_log nvarchar(254),
    name_log      nvarchar(254),
    ip_log        nvarchar(254),
    order_date    nvarchar(254),
    create_time   nvarchar(254),
    code          nvarchar(254),
    content       nvarchar(254)
)
go

create table dbo.sys_menu
(
    id     nvarchar(254),
    name   nvarchar(254),
    url    nvarchar(254),
    status nvarchar(254),
    type   nvarchar(254),
    seq    nvarchar(254),
    sortno int,
    icon   nvarchar(254),
    remark nvarchar(254),
    pid    nvarchar(254),
    code   nvarchar(254)
)
go

create table dbo.sys_role
(
    id     nvarchar(254),
    name   nvarchar(254),
    des    nvarchar(254),
    status nvarchar(254),
    sortno int,
    code   nvarchar(254)
)
go

create index _WA_Sys_id_01D345B0
    on dbo.sys_role (id)
go

create table dbo.sys_role_button
(
    id        nvarchar(254),
    role_id   nvarchar(254),
    button_id nvarchar(254),
    menu_id   nvarchar(254)
)
go

create table dbo.sys_role_menu
(
    id      nvarchar(254),
    role_id nvarchar(254),
    menu_id nvarchar(254)
)
go

create index _WA_Sys_role_id_03BB8E22
    on dbo.sys_role_menu (role_id)
go

create table dbo.sys_user
(
    id               nvarchar(254) not null
        primary key nonclustered,
    name             nvarchar(254),
    pwd              nvarchar(254),
    status           nvarchar(254),
    create_time      nvarchar(254),
    real_name        nvarchar(254),
    phone            nvarchar(254),
    role_id          nvarchar(254),
    customer_status  nvarchar(254),
    area_id          nvarchar(254),
    manager_customer nvarchar(254)
)
go

create index _WA_Sys_name_04AFB25B
    on dbo.sys_user (name)
go

create index _WA_Sys_role_id_04AFB25B
    on dbo.sys_user (role_id)
go

create table dbo.sys_user_role
(
    id      nvarchar(254),
    user_id nvarchar(254),
    role_id nvarchar(254)
)
go

create index _WA_Sys_user_id_09746778
    on dbo.sys_user_role (user_id)
go

create table dbo.tb_monitor_meter
(
    id      int identity(10, 1)
        constraint PK_tb_monitor_meter_id
            primary key nonclustered,
    aguid   char(36),
    m_ctime datetime,
    m_count int,
    b0      varchar
)
go

create table dbo.tb_net_price_log
(
    id         int identity(10, 1)
        constraint PK_tb_net_price_log_id
            primary key nonclustered,
    cTime      datetime,
    piceBefore decimal(18, 3),
    piceAlter  decimal(18, 3),
    cName      varchar(50),
    cLog       varchar(50)
)
go

create table dbo.tb_plan
(
    id           int identity(10, 1)
        constraint PK_tb_plan_id
            primary key nonclustered,
    p_name       varchar(50),
    p_ctime      datetime,
    p_etime      datetime,
    p_utime      datetime,
    p_type       int,
    p_consignor  varchar(50),
    p_consignee  varchar(50),
    p_end_status int,
    p_operator   varchar(50),
    p_remark     varchar(100)
)
go

create table dbo.tb_plan_ex
(
    id                int identity(10, 1)
        constraint PK_tb_plan_ex_id
            primary key nonclustered,
    p_id              int,
    pex_number        varchar(50),
    pex_ctime         datetime,
    pex_etime         datetime,
    pex_goods         varchar(50),
    pex_total_weight  numeric(18, 3),
    pex_finish_weight numeric(18, 3),
    pex_end_status    int,
    pex_operator      varchar(50),
    pex_remark        varchar(100)
)
go

create table dbo.tb_plan_ex_log
(
    id                     int identity(10, 1)
        constraint PK_tb_plan_ex_log_id
            primary key nonclustered,
    c_p_id                 int,
    c_pex_number           varchar(50),
    c_pex_ctime            datetime,
    c_quantity_weight      numeric(18, 3),
    c_old_pex_total_weight numeric(18, 3),
    c_old_pex_end_status   int,
    c_pex_total_weight     numeric(18, 3),
    c_pex_end_status       int,
    c_operator             varchar(50),
    c_remark               varchar(100)
)
go

create table dbo.tb_weight_graphs
(
    id        int identity(10, 1)
        constraint PK_tb_weight_graphs_id
            primary key nonclustered,
    cTime     datetime,
    wID       varchar(50),
    wTypde    int,
    gNumber   int,
    gNumberID int,
    gData     varchar(200)
)
go

create table dbo.tbl_back_record
(
    ID          int identity(10, 1)
        constraint PK_tbl_back_record
            primary key nonclustered,
    weight      numeric(18, 3),
    weight_time datetime,
    flag        bit
)
go

create table dbo.tbl_card
(
    ID        int identity(10, 1)
        constraint PK_tbl_card
            primary key nonclustered,
    cardNum   nvarchar(50),
    carNum    nvarchar(50),
    tare      numeric(18, 3),
    faHuo     nvarchar(50),
    shouHuo   nvarchar(50),
    goods     nvarchar(50),
    spec      nvarchar(50),
    bundle    numeric(18, 3),
    scale     numeric(18, 3),
    price     numeric(18, 3),
    memo      nvarchar(50),
    backup1   nvarchar(50),
    backup2   nvarchar(50),
    backup3   nvarchar(50),
    backup4   nvarchar(50),
    backup5   nvarchar(50),
    backup10  nvarchar(50),
    backup11  nvarchar(50),
    backup12  nvarchar(50),
    backup13  nvarchar(50),
    backup14  nvarchar(50),
    type      int,
    StartTime datetime,
    EndTime   datetime
)
go

create table dbo.tbl_msg
(
    ID          int identity(10, 1)
        constraint PK_tbl_msg
            primary key nonclustered,
    ip          nvarchar(20),
    update_time datetime default getdate(),
    [user]      nvarchar(50),
    content     nvarchar(255)
)
go

create table dbo.tbl_pay_history
(
    ID         int identity(10, 1)
        constraint PK_tbl_pay_history
            primary key nonclustered,
    shouHuo    nvarchar(50),
    updateTime datetime,
    price      decimal(19, 4),
    pay_type   int,
    b0         char,
    aguid      char(36),
    balance    decimal(19, 4)
)
go

create table dbo.tbl_price
(
    id      numeric(18, 0) identity(1, 1)
        primary key nonclustered,
    shouHuo numeric not null,
    goods   numeric not null,
    price   numeric(18, 3) default (0)
)
go

create table dbo.tbl_recvsyn
(
    id    int identity(10, 1)
        constraint PK_tblrecvsyn
            primary key nonclustered,
    aguid char(36)
)
go

create table dbo.tbl_syn
(
    id          int identity(10, 1)
        constraint PK_tbl_syn
            primary key nonclustered,
    aguid       char(36),
    needsyn     char,
    b0          char,
    create_date datetime
)
go

create table dbo.tbl_weight_img
(
    ID         int not null,
    WEIGHT_ID  nvarchar(50),
    GROSS_IMG1 image,
    GROSS_IMG2 image,
    GROSS_IMG3 image,
    GROSS_IMG4 image,
    GROSS_IMG5 image,
    GROSS_IMG6 image,
    TARE_IMG1  image,
    TARE_IMG2  image,
    TARE_IMG3  image,
    TARE_IMG4  image,
    TARE_IMG5  image,
    TARE_IMG6  image
)
go

create table dbo.tbl_weight_img_Invalid
(
    id         int identity(10, 1)
        constraint PK_tbl_weight_img_Invalid_id
            primary key nonclustered,
    WEIGHT_ID  varchar(50),
    new_ID     varchar(50),
    cTime      datetime,
    GROSS_IMG1 image,
    GROSS_IMG2 image,
    GROSS_IMG3 image,
    GROSS_IMG4 image,
    TARE_IMG1  image,
    TARE_IMG2  image,
    TARE_IMG3  image,
    TARE_IMG4  image
)
go

create table dbo.tbl_weightfee
(
    company_code      nvarchar(50) not null
        constraint pk_tbl_weightfee
            primary key nonclustered,
    use_cost          int            default (0),
    manual_input_cost int            default (0),
    cost_type         int            default (0),
    cost_type_one     int            default (0),
    point             int            default (2),
    charge_type       int            default (0),
    grade1            decimal(18, 6) default (0),
    grade2            decimal(18, 6) default (0),
    grade3            decimal(18, 6) default (0),
    grade4            decimal(18, 6) default (0),
    grade5            decimal(18, 6) default (0),
    grade6            decimal(18, 6) default (0),
    init_cost         decimal(18, 6) default (0),
    unit_cost1        decimal(18, 6) default (0),
    unit_cost2        decimal(18, 6) default (0),
    ladder            int            default (0),
    zero_notcost      int            default (0),
    grade11           decimal(18, 6) default (0),
    grade12           decimal(18, 6) default (0),
    grade21           decimal(18, 6) default (0),
    grade22           decimal(18, 6) default (0),
    grade31           decimal(18, 6) default (0),
    grade32           decimal(18, 6) default (0),
    grade41           decimal(18, 6) default (0),
    grade42           decimal(18, 6) default (0),
    grade51           decimal(18, 6) default (0),
    grade52           decimal(18, 6) default (0),
    grade61           decimal(18, 6) default (0),
    grade62           decimal(18, 6) default (0),
    grade71           decimal(18, 6) default (0),
    grade72           decimal(18, 6) default (0),
    grade81           decimal(18, 6) default (0),
    grade82           decimal(18, 6) default (0),
    grade91           decimal(18, 6) default (0),
    grade92           decimal(18, 6) default (0),
    grade101          decimal(18, 6) default (0),
    grade102          decimal(18, 6) default (0),
    grade111          decimal(18, 6) default (0),
    grade112          decimal(18, 6) default (0),
    grade121          decimal(18, 6) default (0),
    grade122          decimal(18, 6) default (0),
    grade131          decimal(18, 6) default (0),
    grade132          decimal(18, 6) default (0),
    cost1             decimal(18, 6) default (0),
    cost2             decimal(18, 6) default (0),
    cost3             decimal(18, 6) default (0),
    cost4             decimal(18, 6) default (0),
    cost5             decimal(18, 6) default (0),
    cost6             decimal(18, 6) default (0),
    cost7             decimal(18, 6) default (0),
    cost8             decimal(18, 6) default (0),
    cost9             decimal(18, 6) default (0),
    cost10            decimal(18, 6) default (0),
    cost11            decimal(18, 6) default (0),
    cost12            decimal(18, 6) default (0),
    cost13            decimal(18, 6) default (0),
    update_time       datetime,
    update_by         nvarchar(20)
)
go

create table dbo.wg_random
(
    random_num     varchar(64),
    type           varchar(255),
    create_day     varchar(255),
    dict_no_prefix varchar(255),
    remark         varchar(255),
    dict_name      varchar(255),
    create_time    varchar(64)
)
go

create table dbo.wg_slave_detail
(
    id          varchar(255),
    type        varchar(255),
    serial_name varchar(255),
    create_time varchar(255),
    update_time varchar(255),
    serial_sort int,
    slave_id    varchar(255),
    status      varchar(255)
)
go

create index _WA_Sys_slave_id_05A3D694
    on dbo.wg_slave_detail (slave_id)
go

create index _WA_Sys_status_05A3D694
    on dbo.wg_slave_detail (status)
go

create table dbo.wg_slave_info
(
    id            nvarchar(254),
    slave_ip      nvarchar(254),
    slave_name    nvarchar(254),
    slave_code    nvarchar(254),
    coil_name     nvarchar(254),
    coil_num      int,
    discrete_name nvarchar(254),
    discrete_num  int,
    create_time   nvarchar(254),
    remark        nvarchar(254),
    status        nvarchar(254)
)
go

create table dbo.wg_user_slave
(
    id          nvarchar(254),
    slave_id    nvarchar(254),
    user_id     nvarchar(254),
    create_time nvarchar(254)
)
go

create table dbo.wg_weigh_record
(
    id          nvarchar(254),
    car_no      nvarchar(254),
    weight      nvarchar(254),
    create_time nvarchar(254),
    type        nvarchar(254)
)
go

create table dbo.作废信息
(
    序号             int identity(10, 1),
    流水号           nvarchar(50) not null
        constraint PK_作废信息
            primary key nonclustered,
    车号             nvarchar(50),
    过磅类型         nvarchar(5),
    发货单位         nvarchar(50),
    收货单位         nvarchar(50),
    货名             nvarchar(50),
    规格             nvarchar(50),
    毛重             numeric(18, 3),
    皮重             numeric(18, 3),
    净重             numeric(18, 3),
    扣重             numeric(18, 3),
    实重             numeric(18, 3),
    单价             numeric(18, 3),
    金额             numeric(18, 3),
    折方系数         numeric(18, 3),
    方量             numeric(18, 3),
    过磅费           numeric(18, 3),
    毛重司磅员       nvarchar(50),
    皮重司磅员       nvarchar(50),
    毛重磅号         nvarchar(10),
    皮重磅号         nvarchar(10),
    毛重时间         datetime,
    皮重时间         datetime,
    一次过磅时间     datetime,
    二次过磅时间     datetime,
    更新人           nvarchar(50),
    更新时间         datetime,
    备注             nvarchar(50),
    打印次数         int,
    上传否           bit          not null,
    备用1            nvarchar(50),
    备用2            nvarchar(50),
    备用3            nvarchar(50),
    备用4            nvarchar(50),
    备用5            nvarchar(50),
    备用6            numeric(18, 3),
    备用7            numeric(18, 3),
    备用8            numeric(18, 3),
    备用9            numeric(18, 3),
    备用10           nvarchar(50),
    备用11           nvarchar(50),
    备用12           nvarchar(50),
    备用13           nvarchar(50),
    备用14           nvarchar(50),
    备用15           numeric(18, 3),
    备用16           numeric(18, 3),
    备用17           numeric(18, 3),
    备用18           numeric(18, 3),
    一次过磅重       numeric(18, 3),
    二次过磅重       numeric(18, 3),
    PlanNumber       varchar(50),
    客户类型         int,
    RecordCreateMode int default (0),
    RecordFinish     int default (0),
    原流水号         varchar(50)
)
go

create table dbo.发货单位
(
    序号     int identity(10, 1),
    代码     nvarchar(50),
    发货单位 nvarchar(50) not null
        constraint PK_发货单位
            primary key nonclustered,
    修正     numeric(18, 3)
)
go

create table dbo.备用1
(
    序号  int identity(10, 1),
    代码  nvarchar(50),
    备用1 nvarchar(50) not null
        constraint PK_备用1
            primary key nonclustered,
    修正  numeric(18, 3)
)
go

create table dbo.备用10
(
    序号   int identity(10, 1),
    代码   nvarchar(50),
    备用10 nvarchar(50) not null
        constraint PK_备用10
            primary key nonclustered,
    修正   numeric(18, 3)
)
go

create table dbo.备用11
(
    序号   int identity(10, 1),
    代码   nvarchar(50),
    备用11 nvarchar(50) not null
        constraint PK_备用11
            primary key nonclustered,
    修正   numeric(18, 3)
)
go

create table dbo.备用12
(
    序号   int identity(10, 1),
    代码   nvarchar(50),
    备用12 nvarchar(50) not null
        constraint PK_备用12
            primary key nonclustered,
    修正   numeric(18, 3)
)
go

create table dbo.备用13
(
    序号   int identity(10, 1),
    代码   nvarchar(50),
    备用13 nvarchar(50) not null
        constraint PK_备用13
            primary key nonclustered,
    修正   numeric(18, 3)
)
go

create table dbo.备用14
(
    序号   int identity(10, 1),
    代码   nvarchar(50),
    备用14 nvarchar(50) not null
        constraint PK_备用14
            primary key nonclustered,
    修正   numeric(18, 3)
)
go

create table dbo.备用2
(
    序号  int identity(10, 1),
    代码  nvarchar(50),
    备用2 nvarchar(50) not null
        constraint PK_备用2
            primary key nonclustered,
    修正  numeric(18, 3)
)
go

create table dbo.备用3
(
    序号  int identity(10, 1),
    代码  nvarchar(50),
    备用3 nvarchar(50) not null
        constraint PK_备用3
            primary key nonclustered,
    修正  numeric(18, 3)
)
go

create table dbo.备用4
(
    序号  int identity(10, 1),
    代码  nvarchar(50),
    备用4 nvarchar(50) not null
        constraint PK_备用4
            primary key nonclustered,
    修正  numeric(18, 3)
)
go

create table dbo.备用5
(
    序号  int identity(10, 1),
    代码  nvarchar(50),
    备用5 nvarchar(50) not null
        constraint PK_备用5
            primary key nonclustered,
    修正  numeric(18, 3)
)
go

create table dbo.套表
(
    车号     nvarchar(50) not null
        constraint PK_套表
            primary key nonclustered,
    发货单位 nvarchar(50),
    收货单位 nvarchar(50),
    货名     nvarchar(50),
    规格     nvarchar(50),
    备用1    nvarchar(50),
    备用2    nvarchar(50),
    备用3    nvarchar(50),
    备用4    nvarchar(50),
    备用5    nvarchar(50),
    备用10   nvarchar(50),
    备用11   nvarchar(50),
    备用12   nvarchar(50),
    备用13   nvarchar(50),
    备用14   nvarchar(50)
)
go

create table dbo.收货单位
(
    序号     int identity(10, 1),
    代码     nvarchar(50),
    收货单位 nvarchar(50) not null
        constraint PK_收货单位
            primary key nonclustered,
    当前金额 decimal(19, 4),
    信用额度 decimal(19, 4),
    修正     numeric(18, 3),
    客户类型 varchar(50),
    b0       char,
    aguid    char(36)
)
go

create table dbo.日志
(
    序号   int identity(10, 1)
        constraint PK_日志
            primary key nonclustered,
    修改人 nvarchar(50),
    时间   datetime,
    日志   nvarchar(255)
)
go

create table dbo.用户信息
(
    序号         int identity(10, 1),
    用户名       nvarchar(50) not null
        constraint PK_用户信息
            primary key nonclustered,
    密码         nvarchar(50),
    预置维护     bit default (0),
    卡号管理     bit default (0),
    系统日志     bit default (0),
    后台记录     bit default (0),
    打印磅单     bit default (0),
    修改磅单     bit default (0),
    打印报表     bit default (0),
    用户管理     bit default (0),
    系统设置     bit default (0),
    界面配置     bit default (0),
    手工重量     bit default (0),
    数据库设置   bit default (0),
    数据备份     bit default (0),
    数据导入     bit default (0),
    数据导出     bit default (0),
    数据清理     bit default (0),
    数据初始化   bit default (0),
    仪表设置     bit default (0),
    视频设置     bit default (0),
    读卡器设置   bit default (0),
    IO模块设置   bit default (0),
    大屏幕设置   bit default (0),
    语音输出     bit default (0),
    数据查询     bit default (0),
    添加记录     bit default (0),
    删除记录     bit default (0),
    修改车号     bit default (0),
    修改发货单位 bit default (0),
    修改收货单位 bit default (0),
    修改货名     bit default (0),
    修改规格     bit default (0),
    修改毛重     bit default (0),
    修改皮重     bit default (0),
    修改扣重     bit default (0),
    修改单价     bit default (0),
    修改折方系数 bit default (0),
    修改过磅费   bit default (0),
    修改备用1    bit default (0),
    修改备用2    bit default (0),
    修改备用3    bit default (0),
    修改备用4    bit default (0),
    修改备用5    bit default (0),
    修改备用6    bit default (0),
    修改备用7    bit default (0),
    修改备用8    bit default (0),
    修改备用9    bit default (0),
    修改备用10   bit default (0),
    修改备用11   bit default (0),
    修改备用12   bit default (0),
    修改备用13   bit default (0),
    修改备用14   bit default (0),
    修改备用15   bit default (0),
    修改备用16   bit default (0),
    修改备用17   bit default (0),
    修改备用18   bit default (0),
    管理员       bit default (0),
    作废记录     bit default (0),
    超限过滤     bit default (0),
    修改流水号   bit default (0),
    修改称重类型 bit default (0),
    修改皮重时间 bit default (0),
    修改毛重时间 bit default (0),
    修改记录     bit default (0),
    多次打印     bit default (0),
    防遥控检测仪 bit default (0),
    价格屏蔽     bit default (0),
    修改记录方式 int
)
go

create table dbo.称重信息
(
    序号             int identity(10, 1),
    流水号           nvarchar(50) not null
        constraint PK_称重信息
            primary key nonclustered,
    车号             nvarchar(50) not null,
    过磅类型         nvarchar(5),
    发货单位         nvarchar(50),
    收货单位         nvarchar(50),
    货名             nvarchar(50),
    规格             nvarchar(50),
    毛重             numeric(18, 3),
    皮重             numeric(18, 3),
    净重             numeric(18, 3),
    扣重             numeric(18, 3),
    实重             numeric(18, 3),
    单价             numeric(18, 3),
    金额             numeric(18, 3),
    折方系数         numeric(18, 3),
    方量             numeric(18, 3),
    过磅费           numeric(18, 3),
    毛重司磅员       nvarchar(50),
    皮重司磅员       nvarchar(50),
    毛重磅号         nvarchar(10),
    皮重磅号         nvarchar(10),
    毛重时间         datetime,
    皮重时间         datetime,
    一次过磅时间     datetime,
    二次过磅时间     datetime,
    更新人           nvarchar(50),
    更新时间         datetime,
    备注             nvarchar(50),
    打印次数         int,
    上传否           bit          not null,
    备用1            nvarchar(50) not null,
    备用2            nvarchar(50),
    备用3            nvarchar(50),
    备用4            nvarchar(50),
    备用5            nvarchar(50),
    备用6            numeric(18, 3),
    备用7            numeric(18, 3),
    备用8            numeric(18, 3),
    备用9            numeric(18, 3),
    备用10           nvarchar(50),
    备用11           nvarchar(50),
    备用12           nvarchar(50),
    备用13           nvarchar(50),
    备用14           nvarchar(50),
    备用15           numeric(18, 3),
    备用16           numeric(18, 3),
    备用17           numeric(18, 3),
    备用18           numeric(18, 3),
    b0               char,
    aguid            char(36),
    客户类型         int,
    e_upimg          char(8),
    RecordCreateMode int,
    备用19           varchar(50),
    备用20           varchar(50),
    备用21           varchar(50),
    备用22           varchar(50),
    备用23           varchar(50),
    备用24           varchar(50),
    备用25           varchar(50),
    备用26           varchar(50),
    备用27           varchar(50),
    备用28           varchar(50),
    driver_info      varchar(36),
    modify_onnet     varchar,
    modify_time      datetime,
    modify_by        varchar(50),
    audit_flag       varchar,
    audit_time       datetime,
    audit_by         varchar(50),
    一次过磅重       numeric(18, 3),
    二次过磅重       numeric(18, 3),
    PlanNumber       varchar(50),
    RecordFinish     int default (0),
    网价同步时间     datetime,
    网价修改人       varchar(50),
    LimitState       int default (0),
    ManyID           varchar(50),
    多次净重         numeric(18, 3)
)
go

create table dbo.规格
(
    序号 int identity(10, 1),
    代码 nvarchar(50),
    规格 nvarchar(50) not null
        constraint PK_规格
            primary key nonclustered,
    修正 numeric(18, 3)
)
go

create table dbo.货名
(
    序号       int identity(10, 1),
    代码       nvarchar(50),
    货名       nvarchar(50) not null
        constraint PK_货名
            primary key nonclustered,
    单价       decimal(19, 4),
    折方系数   numeric(18, 3),
    修正       numeric(18, 3),
    扣重       numeric(18, 3),
    同步时间   datetime,
    修改人     varchar(50),
    更新时间   datetime,
    更新前单价 numeric(18, 3)
)
go

create table dbo.超限信息
(
    序号             int identity(10, 1),
    流水号           nvarchar(50) not null
        constraint PK_超限信息
            primary key nonclustered,
    车号             nvarchar(50),
    过磅类型         nvarchar(5),
    发货单位         nvarchar(50),
    收货单位         nvarchar(50),
    货名             nvarchar(50),
    规格             nvarchar(50),
    毛重             numeric(18, 3),
    皮重             numeric(18, 3),
    净重             numeric(18, 3),
    扣重             numeric(18, 3),
    实重             numeric(18, 3),
    单价             numeric(18, 3),
    金额             numeric(18, 3),
    折方系数         numeric(18, 3),
    方量             numeric(18, 3),
    过磅费           numeric(18, 3),
    毛重司磅员       nvarchar(50),
    皮重司磅员       nvarchar(50),
    毛重磅号         nvarchar(10),
    皮重磅号         nvarchar(10),
    毛重时间         datetime,
    皮重时间         datetime,
    一次过磅时间     datetime,
    二次过磅时间     datetime,
    更新人           nvarchar(50),
    更新时间         datetime,
    备注             nvarchar(50),
    打印次数         int,
    上传否           bit          not null,
    备用1            nvarchar(50),
    备用2            nvarchar(50),
    备用3            nvarchar(50),
    备用4            nvarchar(50),
    备用5            nvarchar(50),
    备用6            numeric(18, 3),
    备用7            numeric(18, 3),
    备用8            numeric(18, 3),
    备用9            numeric(18, 3),
    备用10           nvarchar(50),
    备用11           nvarchar(50),
    备用12           nvarchar(50),
    备用13           nvarchar(50),
    备用14           nvarchar(50),
    备用15           numeric(18, 3),
    备用16           numeric(18, 3),
    备用17           numeric(18, 3),
    备用18           numeric(18, 3),
    一次过磅重       numeric(18, 3),
    二次过磅重       numeric(18, 3),
    PlanNumber       varchar(50),
    客户类型         int,
    RecordCreateMode int default (0),
    RecordFinish     int default (0)
)
go

create table dbo.车号
(
    车号     nvarchar(50) not null
        constraint PK_车号
            primary key nonclustered,
    皮重     numeric(18, 3) default (0),
    备注     nvarchar(50),
    修正     numeric(18, 3),
    净重     numeric(18, 3),
    数量     int,
    上误差   int,
    下误差   int,
    QuickKey varchar(10),
    黑名单   int            default (0)
)
go

create table dbo.验证信息
(
    验证流水号 nvarchar(50) not null
        constraint PK_验证流水号
            primary key nonclustered,
    车号       nvarchar(50),
    毛重       numeric,
    皮重       numeric,
    净重       numeric,
    添加人     nvarchar(50),
    添加时间   datetime,
    验证人1    nvarchar(50),
    验证时间1  datetime,
    验证人2    nvarchar(50),
    验证时间2  datetime,
    备注       nvarchar(50),
    通过       int default (0)
)
go
