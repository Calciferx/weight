package com.calcifer.weight.utils;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;

import java.util.Arrays;
import java.util.List;

public interface LPRSDK extends Library {

    /**加载SDK动态库*/
    /** LPRSDK INSTANCE = (LPRSDK) Native.load("/home/sober/eclipse-workspace/LPRDemo/lib/libVzLPRSDK.so", LPRSDK.class);*/
//	LPRSDK INSTANCE = (LPRSDK) Native.load("E:\\JavaProject\\LPRDemo\\lib\\win64\\VzLPRSDK-x86.dll", LPRSDK.class);


    /**
     * 图像信息
     */
    public static class VZ_LPRC_IMAGE_INFO_Pointer extends Structure {
        public int uWidth;
        /**
         * <宽度
         */
        public int uHeight;
        /**
         * <高度
         */
        public int uPitch;
        /**
         * <图像宽度的一行像素所占内存字节数
         */
        public int uPixFmt;
        /**
         * <图像像素格式，参考枚举定义图像格式（ImageFormatXXX）
         */
        public ByteByReference pBuffer;

        /**
         * <图像内存的首地址
         */

        public static class ByReference extends VZ_LPRC_IMAGE_INFO_Pointer implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPRC_IMAGE_INFO_Pointer implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"uWidth", "uHeight", "uPitch", "uPixFmt", "pBuffer"});
        }
    }

    public static class VZ_TM extends Structure {
        public short nYear;
        /**
         * <年
         */
        public short nMonth;
        /**
         * <月
         */
        public short nMDay;
        /**
         * <日
         */
        public short nHour;
        /**
         * <时
         */
        public short nMin;
        /**
         * <分
         */
        public short nSec;

        /**
         * <秒
         */

        public static class ByReference extends VZ_TM implements Structure.ByReference {
        }

        public static class ByValue extends VZ_TM implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"nYear", "nMonth", "nMDay", "nHour", "nMin", "nSec"});
        }
    }

    public static class VZ_DATE_TIME_INFO extends Structure {
        public int uYear;
        /**
         * <年
         */
        public int nMonth;
        /**
         * <月
         */
        public int nMDay;
        /**
         * <日
         */
        public int nHour;
        /**
         * <时
         */
        public int nMin;
        /**
         * <分
         */
        public int nSec;

        /**
         * <秒
         */

        public static class ByReference extends VZ_DATE_TIME_INFO implements Structure.ByReference {
        }

        public static class ByValue extends VZ_DATE_TIME_INFO implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"nYear", "nMonth", "nMDay", "nHour", "nMin", "nSec"});
        }
    }

    /**
     * 黑白名单中的车辆记录
     */
    public static class VZ_LPR_WLIST_VEHICLE extends Structure {
        public int uVehicleID;
        public byte[] strPlateID = new byte[32];
        /**
         * <车牌字符串
         */
        public int uCustomerID;
        /**
         * <客户在数据库的ID，与VZ_LPR_WLIST_CUSTOMER::uCustomerID对应
         */
        public int bEnable;
        /**
         * <该记录有效标记
         */
        public int bEnableTMEnable;
        /**
         * <是否开启生效时间
         */
        public int bEnableTMOverdule;
        public VZ_TM.ByValue struTMEnable;
        public VZ_TM.ByValue struTMOverdule;
        /**
         * <该记录过期时间,为空表示没有过期时间
         */
        public int bUsingTimeSeg;
        /**
         * <是否使用时间段
         */
        public byte[] byTimeSegOrRange = new byte[228];
        /**
         * <时间段信息
         */
        public int bAlarm;
        /**
         * <是否触发报警（黑名单记录）
         */
        public int iColor;
        /**
         * <车辆颜色
         */
        public int iPlateType;
        /**
         * <车牌类型
         */
        public byte[] strCode = new byte[32];
        /**
         * <车辆编码
         */
        public byte[] strComment = new byte[64];

        /**
         * <车辆编码
         */

        public static class ByReference extends VZ_LPR_WLIST_VEHICLE implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_VEHICLE implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"uVehicleID", "strPlateID", "uCustomerID", "bEnable", "bEnableTMEnable", "bEnableTMOverdule", "struTMEnable", "struTMOverdule",
                    "bUsingTimeSeg", "byTimeSegOrRange", "bAlarm", "iColor", "iPlateType", "strCode", "strComment"});
        }
    }

    public static class VZ_LPR_WLIST_ROW extends Structure {
        public Pointer pCustomer;
        /**
         * <客户，可以为空
         */
        public VZ_LPR_WLIST_VEHICLE.ByReference pVehicle;

        /**
         * <车辆
         */

        public static class ByReference extends VZ_LPR_WLIST_ROW implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_ROW implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"pCustomer", "pVehicle"});
        }
    }

    /**
     * 黑白名单记录的客户信息
     */
    public static class VZ_LPR_WLIST_CUSTOMER extends Structure {
        public int uCustomerID;
        public byte[] strName = new byte[32];
        public byte[] strCode = new byte[32];
        public byte[] reserved = new byte[256];

        public static class ByReference extends VZ_LPR_WLIST_CUSTOMER implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_CUSTOMER implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"uCustomerID", "strName", "strCode", "reserved"});
        }
    }

    /**
     * 批量导入每行的结果
     */
    public static class VZ_LPR_WLIST_IMPORT_RESULT extends Structure {
        public int ret;
        public int error_code;

        public static class ByReference extends VZ_LPR_WLIST_IMPORT_RESULT implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_IMPORT_RESULT implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"ret", "error_code"});
        }
    }

    // 查询条件
    public enum VZ_LPR_WLIST_LIMIT_TYPE {
        VZ_LPR_WLIST_LIMIT_TYPE_ONE,   //查找一条
        VZ_LPR_WLIST_LIMIT_TYPE_ALL,   //查找所有
        VZ_LPR_WLIST_LIMIT_TYPE_RANGE, //查找一段
    }


    public static class VZ_LPR_WLIST_RANGE_LIMIT extends Structure {
        public int startIndex;  //查找起始位置
        public int count;       //查找条数

        public static class ByReference extends VZ_LPR_WLIST_RANGE_LIMIT implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_RANGE_LIMIT implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"startIndex", "count"});
        }
    }

    public static class VZ_LPR_WLIST_LIMIT extends Structure {
        public int limitType;                        //查找条数限制
        public VZ_LPR_WLIST_RANGE_LIMIT.ByReference pRangeLimit;        //查找哪一段数据

        public static class ByReference extends VZ_LPR_WLIST_LIMIT implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_LIMIT implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"limitType", "pRangeLimit"});
        }
    }

    public enum VZ_LPR_WLIST_SEARCH_TYPE {
        VZ_LPR_WLIST_SEARCH_TYPE_LIKE,
        VZ_LPR_WLIST_SEARCH_TYPE_EQUAL
    }

    public static class VZ_LPR_WLIST_SEARCH_CONSTRAINT extends Structure {
        public byte[] key = new byte[32];
        public byte[] search_string = new byte[128];

        public static class ByReference extends VZ_LPR_WLIST_SEARCH_CONSTRAINT implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_SEARCH_CONSTRAINT implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"key", "search_string"});
        }
    }

    //查找条件
    public static class VZ_LPR_WLIST_SEARCH_WHERE extends Structure {
        public int searchType; //查找的方式，如果是完全匹配，每个条件之间为与;是包含字符时，每个条件之间为或
        public int searchConstraintCount;          //查找条件个数，为0表示没有搜索条件
        public VZ_LPR_WLIST_SEARCH_CONSTRAINT.ByReference pSearchConstraints;   //查找条件数组指针

        public static class ByReference extends VZ_LPR_WLIST_SEARCH_WHERE implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_SEARCH_WHERE implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"searchType", "searchConstraintCount", "pSearchConstraints"});
        }
    }

    public static class VZ_LPR_WLIST_LOAD_CONDITIONS extends Structure {
        public VZ_LPR_WLIST_SEARCH_WHERE.ByReference pSearchWhere; //查找条件
        public VZ_LPR_WLIST_LIMIT.ByReference pLimit;       //查找条数限制
        public Pointer pSortType;    //结果的排序方式，为空表示按默认排序

        public static class ByReference extends VZ_LPR_WLIST_LOAD_CONDITIONS implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPR_WLIST_LOAD_CONDITIONS implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"pSearchWhere", "pLimit", "pSortType"});
        }
    }

    /**
     * 识别结果的类型
     */
    public enum VZ_LPRC_RESULT_TYPE {
        VZ_LPRC_RESULT_REALTIME,
        /**
         * <实时识别结果
         */
        VZ_LPRC_RESULT_STABLE,
        /**
         * <稳定识别结果
         */
        VZ_LPRC_RESULT_FORCE_TRIGGER,
        /**
         * <调用“VzLPRClient_ForceTrigger”触发的识别结果
         */
        VZ_LPRC_RESULT_IO_TRIGGER,
        /**
         * <外部IO信号触发的识别结果
         */
        VZ_LPRC_RESULT_VLOOP_TRIGGER,
        /**
         * <虚拟线圈触发的识别结果
         */
        VZ_LPRC_RESULT_MULTI_TRIGGER,
        /**
         * <由_FORCE_\_IO_\_VLOOP_中的一种或多种同时触发，具体需要根据每个识别结果的TH_PlateResult::uBitsTrigType来判断
         */
        VZ_LPRC_RESULT_TYPE_NUM            /**<结果种类个数*/
    }

    public static class VZ_LPRC_VERTEX extends Structure {
        public int X_1000;
        public int Y_1000;

        public static class ByReference extends VZ_LPRC_VERTEX implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPRC_VERTEX implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"X_1000", "Y_1000"});
        }
    }

    public static class VZ_LPRC_VIRTUAL_LOOP_EX extends Structure {
        public byte byID;
        /**
         * 序号
         */
        public byte byEnable;
        /**
         * 是否有效
         */
        public byte byDraw;
        /**
         * 是否绘制
         */
        public byte byRes;
        /**
         * 预留
         */
        public byte[] strName = new byte[32];
        public int uNumVertex;
        public VZ_LPRC_VERTEX[] struVertex = new VZ_LPRC_VERTEX[12];
        /**
         * 顶点数组
         */
        public int eCrossDir;
        /**
         * 穿越方向限制
         */
        public int uTriggerTimeGap;
        /**
         * 对相同车牌的触发时间间隔的限制，单位为秒
         */
        public int uMaxLPWidth;
        /**
         * 最大车牌尺寸限制
         */
        public int uMinLPWidth;

        /**
         * 最小车牌尺寸限制
         */

        public static class ByReference extends VZ_LPRC_VIRTUAL_LOOP_EX implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPRC_VIRTUAL_LOOP_EX implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"byID", "byEnable", "byDraw", "byRes", "strName", "uNumVertex", "struVertex", "eCrossDir", "uTriggerTimeGap", "uMaxLPWidth", "uMinLPWidth"});
        }
    }

    /**
     * 虚拟线圈序列
     */
    public static class VZ_LPRC_VIRTUAL_LOOPS_EX extends Structure {
        public int uNumVirtualLoop;
        public VZ_LPRC_VIRTUAL_LOOP_EX[] struLoop = new VZ_LPRC_VIRTUAL_LOOP_EX[8];

        public static class ByReference extends VZ_LPRC_VIRTUAL_LOOPS_EX implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPRC_VIRTUAL_LOOPS_EX implements Structure.ByValue {
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"uNumVirtualLoop", "struLoop"});
        }
    }


    /**
     * 车辆信息
     */
    public static class CarBrand_Pointer extends Structure {
        public byte brand;
        public byte type;
        public short year;

        public static class ByReference extends CarBrand_Pointer implements Structure.ByReference {
        }

        public static class ByValue extends CarBrand_Pointer implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"brand", "type", "year"});
        }
    }

    /**
     * 车牌坐标
     */
    public static class TH_RECT_Pointer extends Structure {
        public int left;
        public int top;
        public int right;
        public int bottom;

        public static class ByReference extends TH_RECT_Pointer implements Structure.ByReference {
        }

        public static class ByValue extends TH_RECT_Pointer implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"left", "top", "right", "bottom"});
        }
    }

    public static class VZ_TIMEVAL_Pointer extends Structure {
        public int uTVSec;
        public int uTVUSec;

        public static class ByReference extends VZ_TIMEVAL_Pointer implements Structure.ByReference {
        }

        public static class ByValue extends VZ_TIMEVAL_Pointer implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"uTVSec", "uTVUSec"});
        }
    }

    /**
     * 分解时间
     */
    public static class VzBDTime_Pointer extends Structure {
        public byte bdt_sec;
        /**
         * <秒，取值范围[0,59]
         */
        public byte bdt_min;
        /**
         * <分，取值范围[0,59]
         */
        public byte bdt_hour;
        /**
         * <时，取值范围[0,23]
         */
        public byte bdt_mday;
        /**
         * <一个月中的日期，取值范围[1,31]
         */
        public byte bdt_mon;
        /**
         * <月份，取值范围[1,12]
         */
        public byte[] res1 = new byte[3];
        /**
         * <预留
         */
        public int bdt_year;
        /**
         * <年份
         */
        public byte[] res2 = new byte[4];

        /**
         * <预留
         */

        public static class ByReference extends VzBDTime_Pointer implements Structure.ByReference {
        }

        public static class ByValue extends VzBDTime_Pointer implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"bdt_sec", "bdt_min", "bdt_hour", "bdt_mday", "bdt_mon", "res1", "bdt_year", "res2"});
        }
    }

    public static class VZ_LPRC_OSD_Param extends Structure {
        public byte dstampenable;
        /**
         * 0 off 1 on
         */
        public int dateFormat;
        /**
         * 0:YYYY/MM/DD;1:MM/DD/YYYY;2:DD/MM/YYYY
         */
        public int datePosX;
        public int datePosY;
        public byte tstampenable;
        /**
         * 0 off 1 on
         */
        public int timeFormat;
        /**
         * 0:12Hrs;1:24Hrs
         */
        public int timePosX;
        public int timePosY;
        public byte nLogoEnable;
        /**
         * 0 off 1 on
         */
        public int nLogoPositionX;
        /**
         * <  logo position
         */
        public int nLogoPositionY;
        /**
         * <  logo position
         */
        public byte nTextEnable;
        /**
         * 0 off 1 on
         */
        public int nTextPositionX;
        /**
         * <  text position
         */
        public int nTextPositionY;
        /**
         * <  text position
         */
        public byte[] overlaytext = new byte[16];

        /**
         * user define text           	//user define text
         */

        public static class ByReference extends VZ_LPRC_OSD_Param implements Structure.ByReference {
        }

        public static class ByValue extends VZ_LPRC_OSD_Param implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"dstampenable", "dateFormat", "datePosX", "datePosY", "tstampenable", "timeFormat", "timePosX", "timePosY", "nLogoEnable", "nLogoPositionX", "nLogoPositionY",
                    "nTextEnable", "nTextPositionX", "nTextPositionY", "overlaytext"});
        }
    }

    /**
     * 识别结果结构体
     */
    public static class TH_PlateResult_Pointer extends Structure {
        public byte[] license = new byte[16];
        /**
         * 车牌号码
         */
        public byte[] color = new byte[8];
        /**
         * 车牌颜色
         */
        public int nColor;
        /**
         * 车牌颜色序
         */
        public int nType;
        /**
         * 车牌类型
         */
        public int nConfidence;
        /**
         * 车牌可信度
         */
        public int nBright;
        /**
         * 亮度评价
         */
        public int nDirection;
        /**
         * 运动方向，0 unknown, 1 left, 2 right, 3 up , 4 down
         */
        public TH_RECT_Pointer.ByValue rcLocation;
        /**
         * 车牌位置  error
         */
        public int nTime;
        /**
         * 识别时间
         */
        public VZ_TIMEVAL_Pointer.ByValue tvPTS;
        public int uBitsTrigType;
        public byte nCarBright;
        /**
         * 车的亮度
         */
        public byte nCarColor;
        /**
         * 车的颜色
         */
        public byte[] reserved0 = new byte[2];
        public int uId;
        public VzBDTime_Pointer.ByValue struBDTime;
        public byte nIsEncrypt;
        /**
         * <车牌是否加密
         */
        public byte nPlateTrueWidth;
        /**
         * <车牌的真实宽度，单位cm
         */
        public byte nPlateDistance;
        /**
         * <车牌距离相机的位置，单位dm(分米)
         */
        public byte nIsFakePlate;
        /**
         * 是否是伪车牌
         */
        public TH_RECT_Pointer.ByValue car_location;
        /**
         * <车头位置
         */
        public CarBrand_Pointer.ByValue car_brand;
        /**
         * <车辆品牌
         */
        public byte[] featureCode = new byte[20];
        /**
         * 车辆特征码
         */
        public byte[] reserved = new byte[24];


        public static class ByReference extends TH_PlateResult_Pointer implements Structure.ByReference {
        }

        public static class ByValue extends TH_PlateResult_Pointer implements Structure.ByValue {
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList(new String[]{"license", "color", "nColor", "nType", "nConfidence", "nBright", "nDirection", "rcLocation", "nTime", "tvPTS", "uBitsTrigType", "nCarBright", "nCarColor",
                    "reserved0", "uId", "struBDTime", "nIsEncrypt", "nPlateTrueWidth", "nPlateDistance", "nIsFakePlate", "car_location", "car_brand", "featureCode", "reserved"});
        }
    }

    /**
     * @return 0表示成功，-1表示失败
     * @brief 全局初始化
     * @note 在所有接口调用之前调用
     * @ingroup group_global
     */
    int VzLPRClient_Setup();

    /**
     * @brief 全局释放
     * @note 在程序结束时调用，释放SDK的资源
     * @ingroup group_global
     */
    void VzLPRClient_Cleanup();

    /**
     * @param [IN] pStrIP 设备的IP地址
     * @param [IN] wPort 设备的端口号
     * @param [IN] pStrUserName 访问设备所需用户名
     * @param [IN] pStrPassword 访问设备所需密码
     * @return 返回设备的操作句柄，当打开失败时，返回0
     * @brief 打开一个设备
     * @ingroup group_device
     */
    int VzLPRClient_Open(String pStrIP, int wPort, String pStrUserName, String pStrPassword);

    /**
     * @param [IN] pStrIP 设备的IP地址
     * @param [IN] wPort 设备的端口号
     * @param [IN] pStrUserName 访问设备所需用户名
     * @param [IN] pStrPassword 访问设备所需密码
     * @param [IN] wRtspPort 流媒体的端口号,默认为8557(如果为0表示使用默认端口）
     * @param [IN] network_type 网络类型(0局域网,1外网-PDNS方式)
     * @param [IN] sn 设备序列号
     * @param [IN] app_id 安全验证id
     * @param [IN] app_key 安全验证key
     * @return 返回设备的操作句柄，当打开失败时，返回0
     * @brief 打开一个设备，支持外网访问设备
     * @ingroup group_device
     */
    int VzLPRClient_OpenV2(String pStrIP, int wPort, String pStrUserName, String pStrPassword, int wRtspPort, int network_type, String sn);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @return 0表示成功，-1表示失败
     * @brief 关闭一个设备
     * @ingroup group_device
     */
    int VzLPRClient_Close(int handle);

    /**
     * @param [IN]     handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN/OUT] pStatus 输入获取状态的变量地址，输出内容为 1已连上，0未连上
     * @return 0表示成功，-1表示失败
     * @brief 获取连接状态
     * @note 用户可以周期调用该函数来主动查询设备是否断线，以及断线后是否恢复连接
     * @ingroup group_device
     */
    int VzLPRClient_IsConnected(int handle, byte[] pStatus);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] nSerialPort 指定使用设备的串口序号：0表示第一个串口，1表示第二个串口
     * @param [IN] func 接收数据的回调函数
     * @param [IN] pUserData 接收数据回调函数的上下文
     * @return 返回透明通道句柄，0表示失败
     * @brief 开启透明通道
     * @ingroup group_device
     */
    int VzLPRClient_SerialStart(int handle, int nSerialPort, VZDEV_SERIAL_RECV_DATA_CALLBACK func, Pointer pUserData);

    /**
     * @param [IN] nSerialHandle 由VzLPRClient_SerialStart函数获得的句柄
     * @param [IN] pData 将要传输的数据块的首地址
     * @param [IN] uSizeData 将要传输的数据块的字节数
     * @return 0表示成功，其他值表示失败
     * @brief 透明通道发送数据
     * @ingroup group_device
     */
    int VzLPRClient_SerialSend(int nSerialHandle, byte[] pData, int uSizeData);

    /**
     * @param [IN] nSerialHandle 由VzLPRClient_SerialStart函数获得的句柄
     * @return 0表示成功，其他值表示失败
     * @brief 透明通道停止发送数据
     * @ingroup group_device
     */
    int VzLPRClient_SerialStop(int nSerialHandle);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] uChnId IO输出的通道号，从0开始
     * @param [IN] nDuration 延时时间，取值范围[500, 5000]毫秒
     * @return 0表示成功，-1表示失败
     * @brief 设置IO输出，并自动复位(用于开闸)
     * @ingroup group_device
     */
    int VzLPRClient_SetIOOutputAuto(int handle, int uChnId, int nDuration);

    int VzLPRClient_GetDeviceActiveStatus(

            int handle

            , int status

            , int times);


    /**
     * @param [IN]  handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN]  rowcount 记录的条数
     * @param [IN]  pRowDatas 记录的内容数组的地址
     * @param [OUT] results 每条数据是否导入成功
     * @return 0表示成功，-1表示失败
     * @brief 向白名单表导入客户和车辆记录
     * @ingroup group_database
     */
    int VzLPRClient_WhiteListImportRows(int handle, int rowcount, VZ_LPR_WLIST_ROW.ByReference pRowDatas, VZ_LPR_WLIST_IMPORT_RESULT.ByReference pResults);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] pVehicle 将要加入的车辆信息，详见结构体定义VZ_LPR_WLIST_VEHICLE
     * @return 0表示成功，-1表示失败
     * @brief 往白名单表中添加一个车辆信息
     * @ingroup group_database
     */
    int VzLPRClient_WhiteListInsertVehicle(int handle, VZ_LPR_WLIST_VEHICLE.ByReference pVehicle);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @return 0表示成功，-1表示失败
     * @brief 清空数据库客户信息和车辆信息
     * @ingroup group_database
     */
    int VzLPRClient_WhiteListClearCustomersAndVehicles(int handle);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] func 识别结果回调函数，如果为NULL，则表示关闭该回调函数的功能
     * @param [IN] pUserData 回调函数中的上下文
     * @param [IN] bEnableImage 指定识别结果的回调是否需要包含截图信息：1为需要，0为不需要
     * @return 0表示成功，-1表示失败
     * @brief 设置识别结果的回调函数
     * @ingroup group_device
     */
    int VzLPRClient_SetPlateInfoCallBack(int handle, VZLPRC_PLATE_INFO_CALLBACK func, Pointer pUserData, int bEnableImage);

    /**
     * @param [IN] pImgInfo 图像结构体，目前只支持默认的格式，即ImageFormatRGB
     * @param [IN] pFullPathName 设带绝对路径和JPG后缀名的文件名字符串
     * @param [IN] nQuality JPEG压缩的质量，取值范围1~100；
     * @return 0表示成功，-1表示失败
     * @brief 将图像保存为JPEG到指定路径
     * @note 给定的文件名中的路径需要存在
     * @ingroup group_global
     */
    int VzLPRClient_ImageSaveToJpeg(VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgInfo, String pFullPathName, int nQuality);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] pDTInfo 将要设置的设备日期时间信息，详见定义 VZ_DATE_TIME_INFO
     * @return 返回值为0表示成功，返回-1表示失败
     * @brief 设置设备的日期时间
     * @ingroup group_device
     */
    int VzLPRClient_SetDateTime(int handle, VZ_DATE_TIME_INFO.ByReference pDTInfo);

    /**
     * @param [IN]      handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN]      id     车牌记录的ID
     * @param [IN]      pdata  存储图片的内存
     * @param [IN][OUT] size 为传入传出值，传入为图片内存的大小，返回的是获取到jpg图片内存的大小
     * @return 返回值为0表示成功，返回-1表示失败
     * @brief 根据ID获取车牌图片
     * @ingroup group_device
     */
    int VzLPRClient_LoadImageById(int handle, int id, byte[] pdata, int[] size);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] strPlateID 车牌号码
     * @return 0表示成功，-1表示失败
     * @brief 从数据库删除车辆信息
     * @ingroup group_database
     */
    int VzLPRClient_WhiteListDeleteVehicle(int handle, byte[] strPlateID);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] pVirtualLoops 虚拟线圈的结构体指针
     * @return 0表示成功，-1表示失败
     * @brief 设置虚拟线圈，线圈支持更多的顶点
     * @ingroup group_device
     */
    int VzLPRClient_SetVirtualLoopEx(int handle, VZ_LPRC_VIRTUAL_LOOPS_EX.ByReference pVirtualLoops);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] pVirtualLoops 虚拟线圈的结构体指针
     * @return 0表示成功，-1表示失败
     * @brief 获取已设置的虚拟线圈，线圈支持更多的顶点
     * @ingroup group_device
     */
    int VzLPRClient_GetVirtualLoopEx(int handle, VZ_LPRC_VIRTUAL_LOOPS_EX.ByReference pVirtualLoops);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @return 返回值为0表示成功，返回-1表示失败
     * @brief 设置视频OSD参数；
     * @ingroup group_device
     */
    int VzLPRClient_SetOsdParam(int handle, VZ_LPRC_OSD_Param.ByReference pParam);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @return 返回值为0表示成功，返回-1表示失败
     * @brief 获取视频OSD参数；
     * @ingroup group_device
     */
    int VzLPRClient_GetOsdParam(int handle, VZ_LPRC_OSD_Param.ByReference pParam);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @return 0表示成功，-1表示失败
     * @brief 发送软件触发信号，强制处理当前时刻的数据并输出结果
     * @note 车牌识别结果通过回调函数的方式返回，如果当前视频画面中无车牌，则回调函数不会被调用
     * @ingroup group_device
     */
    int VzLPRClient_ForceTrigger(int handle);

    /**
     * @param [IN]  handle 由VzLPRClient_Open函数获得的句柄
     * @param [OUT] ip  设备类型
     * @param [OUT] strength 额外数据长度。
     * @return 返回值为0表示成功，返回其他值表示失败。
     * @brief 根据句柄获取设备的IP
     */
    int VzLPRClient_GetDeviceIP(int handle, byte[] ip, int max_count);

    /**
     * @param [IN] handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN] pFullPathName 图片路径
     * @return 返回值为0表示成功，返回其他值表示失败。
     * @brief 保存抓图数据到Jpeg文件
     */
    int VzLPRClient_SaveSnapImageToJpeg(int handle, String pFullPathName);

    /**
     * @param [IN]  handle 由VzLPRClient_Open函数获得的句柄
     * @param [IN]  pDstBuf 接收图片的缓冲区
     * @param [OUT] uSizeBuf 接收图片的缓冲区长度
     * @return 返回值为大于0表示接收图片的长度，返回其他值表示失败。
     * @brief 保存抓图数据到缓冲区
     */
    int VzLPRClient_GetSnapImage(int handle, byte[] pDstBuf, int uSizeBuf);

    /**
     * @param [IN] handle			由VzLPRClient_Open函数获得的句柄
     * @param [IN] pUserData		回调函数的上下文
     * @param [IN] pResult			车牌信息数组首地址，详见结构体定义 TH_PlateResult
     * @param [IN] uNumPlates		车牌数组中的车牌个数
     * @param [IN] eResultType		车牌识别结果类型，详见枚举类型定义VZ_LPRC_RESULT_TYPE
     * @param [IN] pImgFull		当前帧的图像内容，详见结构体定义VZ_LPRC_IMAGE_INFO
     * @param [IN] pImgPlateClip	当前帧中车牌区域的图像内容数组，其中的元素与车牌信息数组中的对应
     * @brief 通过该回调函数获得车牌识别信息
     * @return 0表示成功，-1表示失败
     * @note 如果需要该回调函数返回截图内容 pImgFull 和pImgPlateClip，需要在设置回调函数（VzLPRClient_SetPlateInfoCallBack）时允许截图内容，否则该回调函数返回的这两个指针为空；
     * @note 实时结果（VZ_LPRC_RESULT_REALTIME）的回调是不带截图内容的
     * @ingroup group_callback
     */
    public static interface VZLPRC_PLATE_INFO_CALLBACK extends Callback {
        public void invoke(int handle, Pointer pUserData, TH_PlateResult_Pointer.ByReference pResult, int uNumPlates,
                           int eResultType, VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgFull, VZ_LPRC_IMAGE_INFO_Pointer.ByReference pImgPlateClip);
    }

    /**
     * @param [IN] nSerialHandle VzLPRClient_SerialStart返回的句柄
     * @param [IN] pRecvData	接收的数据的首地址
     * @param [IN] uRecvSize	接收的数据的尺寸
     * @param [IN] pUserData	回调函数上下文
     * @brief 通过该回调函数获得透明通道接收的数据
     * @ingroup group_callback
     */
    public static interface VZDEV_SERIAL_RECV_DATA_CALLBACK extends Callback {
        public void invoke(int handle, Pointer pRecvData, int uRecvSize, Pointer pUserData);
    }

    /**
     * @param [IN] nSerialHandle VzLPRClient_SerialStart返回的句柄
     * @param [IN] pRecvData	接收的数据的首地址
     * @param [IN] uRecvSize	接收的数据的尺寸
     * @param [IN] pUserData	回调函数上下文
     * @brief 通过该回调函数获得透明通道接收的数据
     * @ingroup group_callback
     */
    public static interface VZLPRC_FIND_DEVICE_CALLBACK_EX extends Callback {
        public void invoke(String pStrDevName, String pStrIPAddr, short usPort1, short usType, long SL, long SH, String netmask, String gateway, Pointer pUserData);
    }

    /**
     * @param [IN] func 找到的设备通过该回调函数返回
     * @param [IN] pUserData 回调函数中的上下文
     * @return 0表示成功，-1表示失败
     * @brief 开始查找设备
     * @ingroup group_global
     */
    int VZLPRClient_StartFindDeviceEx(VZLPRC_FIND_DEVICE_CALLBACK_EX func, Pointer pUserData);

    /**
     * @brief 停止查找设备
     * @ingroup group_global
     */
    void VZLPRClient_StopFindDevice();

    public static interface VZLPRC_WLIST_QUERY_CALLBACK extends Callback {
        public void invoke(int cbtype, VZ_LPR_WLIST_VEHICLE.ByReference vehicle, VZ_LPR_WLIST_CUSTOMER.ByReference pCustomer, Pointer UserData);
    }

    int VzLPRClient_WhiteListSetQueryCallBack(int handle, VZLPRC_WLIST_QUERY_CALLBACK callback, Pointer pUserData);

    int VzLPRClient_WhiteListQueryVehicleByPlate(int handle, byte[] strPlateID);


    int VzLPRClient_WhiteListGetVehicleCount(int handle, int count[], VZ_LPR_WLIST_SEARCH_WHERE.ByReference pSearchWhere);

    int VzLPRClient_WhiteListLoadVehicle(int handle, VZ_LPR_WLIST_LOAD_CONDITIONS.ByReference pLoadCondition);

}
