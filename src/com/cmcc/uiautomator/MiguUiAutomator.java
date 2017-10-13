package com.cmcc.uiautomator;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.android.uiautomator.core.UiCollection;
import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiScrollable;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.core.UiWatcher;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import redis.clients.jedis.Jedis;

/**
 * Created by Admin on 2017/8/27.
 */
public class MiguUiAutomator extends UiAutomatorTestCase {

	private static final String TAG = MiguUiAutomator.class.getSimpleName();
	// private static Logger mLogger;
	private static final String FORMAT_LOG = "---> %s [%s] %s";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat KEY_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final Point[] POINT = { new Point(120, 550), new Point(400, 550), new Point(680, 550),
			new Point(120, 1000), new Point(400, 1000), new Point(680, 1000) };
	private static final int PAY_POINT[] = { 258, 508, 788, 1068, 1258 };
	private static final String[] PREFERENCE_SETTING = { "publish_img1", "boy_img3", "girl_img2" };
	private static Point endPoint = new Point();
	private static String redis_ip;
	private static String redis_key;
	private static String file_path = "";
	private static Jedis jedis;
	private static int resultCode = 0;
	private static String resultMsg = "";
	private static String userName;
	private static String password;
	private static String imei = "unknown";
	private static String macAddress = "unknown";
	private static String channelId = "unkown";
	private static long startTime;
	private static long endTime;
	private static UserInfo userInfo;
	private static DeviceInfo deviceInfo;
	private static int userType = 3;// 默认账号类型为无账号
	private static int taskType = 1;// 默认任务类型为PV操作
	private static int setOrNot = 0;// 默认不设置指定参数
	private static int preBalance = 0;// 预支付金额
	private static int actualBalance = 0; // 实际支付金额
	private static int count = 0; // 用户数
	private static int pay_point_index = 0;
	private static boolean valid = false;

	@Override
	protected void setUp() throws Exception {
		log("setUp of " + getName());
		redis_ip = getParams().getString("ip");
		setAssert("please tell me a valid ip address of redis", (!TextUtils.isEmpty(redis_ip)));
		log("redis_ip=" + redis_ip);
		redis_key = getParams().getString("key");
		setAssert("please tell me a valid account key of redis", (!TextUtils.isEmpty(redis_key)));
		log("redis_key=" + redis_key);
		file_path = Environment.getDataDirectory() + "/data/" + GlobalConsts.PACKAGE_NAME
				+ "/shared_prefs/CMReader.xml";
		jedis = new Jedis(redis_ip, GlobalConsts.REDIS_PORT);
		jedis.auth(GlobalConsts.REDIS_PWD);
		log("当前版本：" + GlobalConsts.RELEASE_VERSION);
		if (taskType == 1) {
			log("阅读书目数量：" + GlobalConsts.BOOK_COUNT);
			log("单本阅读页数：" + GlobalConsts.PAGE_COUNT);
			log("翻页时间间隔：" + GlobalConsts.PAGE_TURNING_TIME_INTERVAL + "ms");
		}
		// config();
		init();
		registerUiWatcher();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		log("tearDown of " + getName());
		super.tearDown();
	}

	private static void setAssert(String des, boolean exist) {
		resultMsg = des;
		// assertTrue(des, exist);
	}

	private static void init() {
		endPoint.x = UiDevice.getInstance().getDisplayWidth();
		endPoint.y = UiDevice.getInstance().getDisplayHeight();
		// initLog4j();
	}

	// private static void initLog4j() {
	// LogConfigurator logConfigurator = new LogConfigurator();
	// logConfigurator.setFileName(Environment.getExternalStorageDirectory() +
	// File.separator + redis_key + ".log");
	// logConfigurator.setRootLevel(Level.DEBUG);
	// logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
	// logConfigurator.setLevel("org.apache", Level.ERROR);
	// logConfigurator.setMaxFileSize(1024 * 1024 * 2);
	// logConfigurator.setImmediateFlush(true);
	// logConfigurator.configure();
	// mLogger = Logger.getLogger(TAG);
	// }

	/*
	 * 注册UiWatcher
	 **/
	private static void registerUiWatcher() {
		UiDevice.getInstance().registerWatcher("ANR", new UiWatcher() {
			@Override
			public boolean checkForCondition() {
				// TODO Auto-generated method stub
				UiObject mMessage = new UiObject(new UiSelector().resourceId("android:id/message"));
				UiObject mANRBtn = new UiObject(new UiSelector().resourceId("android:id/button1"));
				try {
					mANRBtn.click();
					if (mMessage.exists()) {
						log("系统弹框提示：" + mMessage.getText());
					}
					return true;
				} catch (UiObjectNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log("非系统提示");
				}
				return false;
			}
		});
	}

	/*
	 * 配置参数
	 **/
	// private static void config() {
	// Configurator.getInstance().setWaitForSelectorTimeout(GlobalConsts.TIME_OUT_FOR_EXISTS)
	// .setScrollAcknowledgmentTimeout(GlobalConsts.TIME_OUT_SCROLL_ACKNOWLEDGEMENT);
	// }

	private static String parseUserObj2Str() {
		if (userInfo == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		json.put("userName", userInfo.getUserName());
		json.put("password", userInfo.getPassword());
		json.put("userType", userInfo.getUserType());
		json.put("taskType", userInfo.getTaskType());
		json.put("customerKey", userInfo.getCustomerKey());
		json.put("createTime", userInfo.getCreateTime());
		json.put("channelId", userInfo.getChannelId());
		json.put("resultCode", userInfo.getResultCode());
		json.put("resultMsg", userInfo.getResultMsg());
		json.put("setOrNot", userInfo.getSetOrNot());
		json.put("duration", userInfo.getDuration());
		json.put("preBalance", userInfo.getPreBalance());
		json.put("actualBalance", userInfo.getActualBalance());
		return json.toJSONString();
	}

	private static String parseDeviceObj2Str() {
		if (deviceInfo == null) {
			return null;
		}
		JSONObject json = new JSONObject();
		json.put("imei", deviceInfo.getImei());
		json.put("macAddr", deviceInfo.getMacAddr());
		json.put("androidId", deviceInfo.getAndroidId());
		json.put("SSID", deviceInfo.getSSID());
		json.put("BSSID", deviceInfo.getBSSID());
		json.put("phoneNum", deviceInfo.getPhoneNum());
		json.put("ICCID", deviceInfo.getICCID());
		json.put("IMSI", deviceInfo.getIMSI());
		json.put("simStatus", deviceInfo.getSimStatus());
		json.put("operatorId", deviceInfo.getOperatorId());
		json.put("operatorName", deviceInfo.getOperatorName());
		json.put("countryCode", deviceInfo.getCountryCode());
		json.put("model", deviceInfo.getModel());
		json.put("manufacture", deviceInfo.getManufacture());
		json.put("hardware", deviceInfo.getHardware());
		json.put("brand", deviceInfo.getBrand());
		return json.toJSONString();
	}

	public void testCase() {
		String accountInfo = null;
		while ((jedis.llen(redis_key) > 0)) {
			log("<------------------------------start-------------------------------->");
			log("当前账号数量:" + jedis.llen(redis_key));
			try {
				log("上一账号是否可用: " + valid);
				if (!valid) {
					accountInfo = jedis.rpop(redis_key);
					if (TextUtils.isEmpty(accountInfo))
						return;
					count++;
					log("获取到第" + count + "个账号:" + accountInfo);
					resolveParams(accountInfo);
				}
				onStart();
				clearCache();
				// setRandomImei();
				setRandomIMEI();
				launchApp();
				preferenceSetting();
				closeSmsDialog();
				parseXML();
				if (userType != 3) {// 非游客账户
					firstToLoginPage();
					firstLogin();
					handleDrawer();
				}
				restoreDeviceInfo();
				valid = false;
				excuteTask();
				resultCode = 1;
				resultMsg = "ok";
			} catch (Exception e) {
				log(e.toString());
			} finally {
				onFinish();
				exitApp();
			}
			log("<------------------------------end-------------------------------->");
		}
	}

	private void onStart() {
		resultCode = 0;
		resultMsg = "";
		startTime = getNowTime();
		valid = ((userType < 3 && setOrNot == 0) ? true : false);
	}

	private void onFinish() {
		deviceInfo = null;
		endTime = getNowTime();
		String duration = (endTime - startTime) / 1000 + "";
		String date = DATE_FORMAT.format(new Date());
		if (userInfo != null && !valid) {
			userInfo.setDuration(duration).setCreateTime(date).setResultCode(resultCode + "").setResultMsg(resultMsg)
					.setTaskType(taskType + "").setCustomerKey(redis_key).setSetOrNot(setOrNot + "");
			String json_result = parseUserObj2Str();
			log("--send result：" + json_result);
			if (setOrNot == 0) {
				jedis.rpush(redis_key + "_" + KEY_DATE_FORMAT.format(new Date()), json_result);
			} else {
				jedis.rpush(redis_key + "_remain" + "_" + KEY_DATE_FORMAT.format(new Date()), json_result);
			}
		}
	}

	private void excuteTask() throws UiObjectNotFoundException {
		if (setOrNot == 1)
			return;
		switch (taskType) {
		case 2:
			pay();
			break;
		default:
			read();
			break;
		}
	}

	private void read() throws UiObjectNotFoundException {
		log("step8:执行阅读任务");
		int index = checkUI();
		int count = ((setOrNot == 1) ? 1 : GlobalConsts.BOOK_COUNT);
		for (int i = 0; i < count; i++) {
			readFreeBook(index);
			index++;
		}
	}

	private static UiScrollable openDownloadPage() throws UiObjectNotFoundException {
		openDailyReadPage();
		while (pay_point_index < 5) {
			openBookDetailPage(PAY_POINT[pay_point_index]);
			UiDevice.getInstance().click(458, 569);
			if (!(count == 1 && pay_point_index != 0)) {
				SystemClock.sleep(500L);
				UiDevice.getInstance().click(458, 569);
			}
			UiScrollable mListView = new UiScrollable(
					new UiSelector().resourceId(GlobalConsts.PACKAGE_NAME + ":id/expendlist"));
			mListView.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
			if (mListView.exists()) {
				return mListView;
			} else {
				UiObject mPayBtn = getUiObjectById("order_pay_view", "pay button");
				if (mPayBtn.exists()) {
					log("当前页面为支付页面");
					UiDevice.getInstance().pressBack();
				}
				log("当前页面为“书籍详情”页面");
				getUiObjectById("titlebar_level_2_back_button", "book detail back button").click();
				pay_point_index++;
			}
		}
		return null;

	}

	private static void openDailyReadPage() throws UiObjectNotFoundException {
		UiObject mLeaguerBtn = new UiObject(new UiSelector().text("会员"));
		mLeaguerBtn.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
		setAssert("home leaguer button not exists", mLeaguerBtn.exists());
		mLeaguerBtn.click();
		getUiObjectById("titlebar_button_search", "leaguer page not exists");
		UiDevice.getInstance().click(640, 495);
		getUiObjectById("titlebar_button_bookstore", "daily reading page not exists");
	}

	private static void openBookDetailPage(int y) {
		UiDevice.getInstance().click(endPoint.x / 2, y);
		SystemClock.sleep(4 * 1000L);
	}

	private void pay() throws UiObjectNotFoundException {
		log("step8:执行支付任务");
		// 查找所有书目的章节
		UiScrollable mListView = openDownloadPage();
		mListView.setAsVerticalList();
		log("是否可滚动:" + mListView.isScrollable());
		if (mListView.isScrollable()) {
			mListView.flingToEnd(mListView.getMaxSearchSwipes());
		}
		// 查找价格显示控件
		UiObject mPrice = getUiObjectById("tv_totalNeed2PayCount", "download page price text");
		// 选择要下载的章节直至付费的价格大于5元
		int index = 1;
		UiObject mItem = null;
		while (Float.parseFloat(mPrice.getText().trim()) * 100 <= preBalance) {
			mItem = mListView.getChild(
					new UiSelector().resourceId(GlobalConsts.PACKAGE_NAME + ":id/checkbox_group").instance(index));
			setAssert("chapter item by index not found", mItem.exists());
			mItem.click();
			index++;
		}
		// 点击“去结算”按钮
		log("点击“去结算”按钮");
		UiObject mDownloadBtn = getUiObjectById("btn_settleUp", "download button");
		mDownloadBtn.clickAndWaitForNewWindow();
		// 查找“选择支付方式”控件并点击
		log("查找“选择支付方式”控件并点击");
		UiObject mPayOption = getUiObjectById("pay_select_layout", "order page pay option view");
		mPayOption.click();
		// 查找“默认支付方式选择”Dialog
		log("查找“默认支付方式选择”Dialog");
		UiObject mPayInfoList = getUiObjectById("payInfoList", "pay option dialog");
		// 查找“书券”支付方式并点击
		log("查找“书券”支付选项");
		UiObject mTicketOption = mPayInfoList
				.getChild((new UiSelector().className("android.widget.RelativeLayout").instance(1)));
		setAssert("ticket option not exists", mTicketOption.exists());
		log("点击“书券”选项");
		mTicketOption.click();
		// 查找“立即支付”按钮并点击
		UiObject mPayBtn = getUiObjectById("order_pay_view", "order page pay button");
		if (mPayBtn.exists() && mPayBtn.getText().trim().contains("立即支付")) {
			UiObject mVipFee = getUiObjectById("vip_fee_view", "order page fee view");
			String mFee = mVipFee.getText().trim().replace("元", "");
			actualBalance = (int) (Float.parseFloat(mFee) * 100);
			userInfo.setActualBalance(actualBalance + "");
			mPayBtn.click();
		} else {
			userInfo.setActualBalance("0");
			setAssert("pay failed because of not sufficient funds", true);
			throw new UiObjectNotFoundException("pay failed because of not sufficient funds");
		}
	}

	private static void resolveParams(String accountInfo) {
		log("step1:解析参数");
		// json字符串转对象
		userInfo = JSONObject.parseObject(accountInfo, UserInfo.class);
		setAssert("userInfo is a null reference", userInfo != null);
		// 任务类型
		taskType = Integer.parseInt(userInfo.getTaskType());
		// 是否设置固定参数
		setOrNot = Integer.parseInt(userInfo.getSetOrNot());
		// 用户类型 userType: 0 手机号；1 邮箱；2 自定义；3 无账号
		userType = Integer.parseInt(userInfo.getUserType());
		// 无账户用户
		if (userType == 3)
			return;
		userName = userInfo.getUserName();
		password = userInfo.getPassword();
		if (taskType == 2) {// 支付
			preBalance = Integer.parseInt(userInfo.getPreBalance());
		}
		// if (setOrNot == 1) {
		String device = jedis.get(userName + "_" + redis_key);
		if (TextUtils.isEmpty(device)) {
			setOrNot = 0;
		} else {
			deviceInfo = JSONObject.parseObject(device, DeviceInfo.class);
			setOrNot = 1;
		}
		// }
	}

	/*
	 * 第一次登陆
	 **/
	private static void firstToLoginPage() throws UiObjectNotFoundException {
		// 点击左上角头像按钮
		UiObject mPersonalBtn = getUiObjectById("recom_btn_bookstore_personal", "personal button");
		mPersonalBtn.click();
		// 查找特定ID的控件集合
		UiObject mTvLogin = getUiObjectById("tv_login", "login text view");
		mTvLogin.clickAndWaitForNewWindow();
	}

	/*
	 * 将XML转为Document对象
	 **/
	private static void parseXML() throws DocumentException, IOException {
		log("step7:解析咪咕阅读客户端本地参数");
		File file = new File(file_path);
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		Element root = document.getRootElement();
		String attributeValue = null;
		String stringValue = null;
		@SuppressWarnings("unchecked")
		List<Element> elements = root.elements();
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			attributeValue = element.attributeValue("name");
			stringValue = element.getStringValue();
			if (attributeValue.equals("device_id")) {
				imei = stringValue;
				if (deviceInfo != null) {
					deviceInfo.setImei(imei);
				}
				log("当前IMEI:" + imei);
			}
			if (attributeValue.equals("device_mac_address")) {
				macAddress = stringValue;
				if (deviceInfo != null) {
					deviceInfo.setMacAddr(macAddress);
				}
				log("当前MAC:" + macAddress);
			}
			if (attributeValue.equals("channel_id")) {
				channelId = stringValue;
				userInfo.setChannelId(channelId);
				log("当前渠道:" + channelId);
			}
		}
	}

	/*
	 * 判断抽屉是否收回
	 **/
	private static void handleDrawer() throws UiObjectNotFoundException {
		UiObject mDrawerLayout = getUiObjectById("drawer_layout_mine", "main drawer layout");
		if (mDrawerLayout.exists()) {
			if (taskType == 2) {
				bTokenCheck();
			}
			UiDevice.getInstance().pressBack();
		}
		UiObject mSearchView = getUiObjectById("recom_btn_search", "main search button");
		if (!mSearchView.exists()) {
			UiObject mLoginError = new UiObject(
					new UiSelector().resourceId(GlobalConsts.PACKAGE_NAME + ":id/tv_main_error_message"));
			mLoginError.waitForExists(5 * 1000L);
			if (mLoginError.exists()) {
				setAssert(mLoginError.getText().trim(), true);
				throw new UiObjectNotFoundException(mLoginError.getText().trim());
			} else {
				setAssert("main top search view", true);
				throw new UiObjectNotFoundException("main top search view");
			}
		}
	}

	private static void bTokenCheck() throws UiObjectNotFoundException {
		UiObject mTicket = getUiObjectById("fl_assets", "ticket item");
		if (mTicket.exists()) {
			UiObject mPrice = mTicket
					.getChild(new UiSelector().resourceId(GlobalConsts.PACKAGE_NAME + ":id/item_tv_detail"));
			String mDes = mPrice.getText().trim();
			if (mDes.contains("元")) {
				log("账户余额：" + mDes);
				mDes = mDes.replace("元", "");
				int value = (int) (Float.parseFloat(mDes) * 100);
				if (value == 0) {
					setAssert("account balance is insufficient", true);
					throw new UiObjectNotFoundException("account balance is insufficient");
				} else {
					preBalance = Math.min(preBalance, value);
				}
			}
		}
	}

	/*
	 * 保存设备信息
	 **/
	private static void restoreDeviceInfo() {
		if (setOrNot == 1)
			return;
		String json_device = parseDeviceObj2Str();
		if (TextUtils.isEmpty(json_device))
			return;
		if (userType == 3) {
			jedis.rpush(KEY_DATE_FORMAT.format(new Date()) + "_" + "imei", json_device);
		} else {
			jedis.set(userName + "_" + redis_key, json_device);
		}
	}

	/*
	 * 打开搜索页面
	 **/
	private static void openSearchPage() throws UiObjectNotFoundException {
		log("查找首页顶部搜索框控件");
		UiObject mSearchView = getUiObjectById("recom_btn_search", "main search button");
		log("点击首页顶部搜索框打开搜索页面");
		if (mSearchView.exists()) {
			UiDevice.getInstance().click(608, 53);
			mSearchView.clickAndWaitForNewWindow();
		} else {
			setAssert("main top search view", true);
			throw new UiObjectNotFoundException("main top search view");
		}
	}

	/*
	 * 通过搜索页面打开“免费频道”页面
	 **/
	private void openFreeChannelBySearch() throws UiObjectNotFoundException {
		openSearchPage();
		UiObject mEdtSearch = getUiObjectById("etSearch", "search edit");
		if (!mEdtSearch.exists()) {
			UiDevice.getInstance().click(endPoint.x / 2, 55);
			mEdtSearch = getUiObjectById("etSearch", "search edit");
		}
		mEdtSearch.setText(Utf7ImeHelper.e("免费频道"));
		UiObject mSearchBtn = getUiObjectById("btn_search", "search button");
		mSearchBtn.clickAndWaitForNewWindow();
		UiObject mWebKit = new UiObject(new UiSelector().className("android.webkit.WebView"));
		mWebKit.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
		setAssert("search result webview page not exists", mWebKit.exists());
		if (mWebKit.exists()) {
			UiDevice.getInstance().click(endPoint.x / 2, 220);
		} else {
			log("搜索结果页面未加载成功");
			throw new UiObjectNotFoundException("search result webview page load failed");
		}
		int k = 0;
		while (true) {
			UiObject mTitle = getUiObjectById("titlebar_text_not_used", "free channel page");
			if (mTitle.exists()) {
				break;
			}
			if (k++ == 0) {
				log("未成功打开免费频道，再次尝试打开");
				UiDevice.getInstance().click(endPoint.x / 2, 220);
			} else {
				throw new UiObjectNotFoundException("free channel page load failed");
			}
		}
	}

	/*
	 * 进入免费频道 查找“今日限免”书目
	 */
	private int checkUI() throws UiObjectNotFoundException {
		openFreeChannelBySearch();
		int index = 9;
		int result = judgePageType();
		if (result == 0) {// 非web页面，直接通过resourceId获取书目
			log("判断今日限免书目是否已经结束");
			UiObject mTextHint = new UiObject(new UiSelector().index(8));
			mTextHint.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
			setAssert("text hint not exists", mTextHint.exists());
			String description = mTextHint.getContentDescription();
			if (description != null && description.contains("已结束")) {
				log("今日限免活动已结束，开始查找“咪咕限免”书目");
				index = 13;
			} else {
				log("今日限免活动未结束，开始查找“今日限免”书目");
			}
			UiObject mFreeBook = new UiObject(new UiSelector().index(index));
			if (!mFreeBook.exists()) {
				UiDevice.getInstance().pressBack();
				checkUI();
			}
			return index;
		} else {// web页面，通过坐标获取书目
			return 0;
		}

	}

	private static int judgePageType() throws UiObjectNotFoundException {
		UiCollection mWebView = new UiCollection(new UiSelector().className("android.webkit.WebView"));
		return (mWebView.exists() ? -1 : 0);
	}

	private static void firstLogin() throws UiObjectNotFoundException {
		// 查找账号输入框
		UiObject mUser = getUiObjectById("sso_login_username_edt", "username edittext");
		mUser.click();
		Rect mUserRect = mUser.getVisibleBounds();
		int user_x = mUserRect.right - 30;
		int user_y = mUserRect.centerY();
		UiDevice.getInstance().click(user_x, user_y);
		mUser.setText(Utf7ImeHelper.e(userName));
		// 查找登录密码输入框
		UiObject mPassword = getUiObjectById("sso_login_password_edt", "password edittext");
		mPassword.click();
		Rect mPasswordRect = mPassword.getVisibleBounds();
		int password_x = mPasswordRect.right - 60;
		int password_y = mPasswordRect.centerY();
		UiDevice.getInstance().click(password_x, password_y);
		mPassword.setText(Utf7ImeHelper.e(password));
		// 点击登录按钮进行登录
		UiObject mLoginBtn = getUiObjectById("sso_login_btn", "login button");
		mLoginBtn.clickAndWaitForNewWindow();
	}

	private static void closeSmsDialog() throws UiObjectNotFoundException {
		log("step6:关闭短信快速登录提示框");
		UiObject mTextHint = getUiObjectById("ckb_never_remind", "never remind check button");
		mTextHint.click();
		UiObject mDialogCancel = getUiObjectById("tv_secondary_choice", "sms dialog");
		mDialogCancel.click();
	}

	/*
	 * 打开咪咕阅读
	 **/
	private static void launchApp() throws IOException {
		// 打开咪咕阅读
		log("step4:打开咪咕阅读");
		Runtime.getRuntime().exec("monkey -p " + GlobalConsts.PACKAGE_NAME + " -v 1");
	}

	/*
	 * 关闭咪咕阅读
	 **/
	private static void exitApp() {
		log("step9:退出咪咕阅读客户端");
		for (int i = 0; i < 6; i++) {
			UiDevice.getInstance().pressBack();
		}
	}

	/*
	 * 清除咪咕阅读缓存
	 */
	private static void clearCache() throws IOException {
		log("step2:清除应用缓存");
		Runtime.getRuntime().exec("pm clear " + GlobalConsts.PACKAGE_NAME);
	}

	/*
	 * 偏好设置
	 **/
	private static void preferenceSetting() throws UiObjectNotFoundException {
		if (setOrNot == 1)
			return;
		log("step5:阅读偏好设置");
		Random random = new Random();
		int index = random.nextInt(3);
		UiObject mPreferenceBtn = getUiObjectById(PREFERENCE_SETTING[index], "preference button");
		mPreferenceBtn.click();
		UiObject mGoBtn = getUiObjectById("start_reading", "start button");
		mGoBtn.click();
	}

	/*
	 * 打开IMEI修改器
	 **/
	private static void launchXposed() throws IOException {
		log("--launch Xposed app");
		Runtime.getRuntime().exec("monkey -p " + GlobalConsts.EXPOSED_PACKAGE_NAME + " -v 1");
	}

	/*
	 * 打开008修改器
	 **/
	// private static void launch008K() throws IOException {
	// log("--launch 008K app");
	// Runtime.getRuntime().exec("monkey -p " + GlobalConsts.EXPOSED_PACKAGE_NAME +
	// " -v 1");
	// }

	/*
	 * 生成随机参数
	 **/
	// private static void setProperty() throws UiObjectNotFoundException {
	// // UiObject mCircle = getUiObjectById("main_centerImg", "008K center img
	// // circle");
	// // mCircle.clickAndWaitForNewWindow();
	// UiObject mNetRandom = getUiObjectById("set_value_fromNet");
	// mNetRandom.click();
	// UiObject mSave = getUiObjectById("button_restore");
	// mSave.click();
	// }

	/*
	 * 设置特定参数并点击保存按钮
	 **/
	private static void setProperties() throws UiObjectNotFoundException {
		log("--set properties");
		if (deviceInfo == null) {
			setOrNot = 0;
		}
		if (setOrNot == 1) {// 给定参数
			setGivenParams(getUiObjectById("imei"), deviceInfo.getImei());
			setGivenParams(getUiObjectById("android_id"), deviceInfo.getAndroidId());
			setGivenParams(getUiObjectById("mac"), deviceInfo.getMacAddr());
			// setGivenParams(getUiObjectById("ssid"), deviceInfo.getSSID());
			// setGivenParams(getUiObjectById("bssid"), deviceInfo.getBSSID());
			setGivenParams(getUiObjectById("phoneNo"), deviceInfo.getPhoneNum());
			setGivenParams(getUiObjectById("simSerial"), deviceInfo.getICCID());
			setGivenParams(getUiObjectById("subscriberId"), deviceInfo.getIMSI());
			// setGivenParams(getUiObjectById("simState"), deviceInfo.getSimStatus());
			// setGivenParams(getUiObjectById("operId"), deviceInfo.getOperatorId());
			// setGivenParams(getUiObjectById("operName"), deviceInfo.getOperatorName());
			// setGivenParams(getUiObjectById("isoCode"), deviceInfo.getCountryCode());
			setGivenParams(getUiObjectById("MODEL"), deviceInfo.getModel());
			setGivenParams(getUiObjectById("MANUFACTURER"), deviceInfo.getManufacture());
			// setGivenParams(getUiObjectById("HARDWARE"), deviceInfo.getHardware());
			setGivenParams(getUiObjectById("BRAND"), deviceInfo.getBrand());
			getUiObjectById("button1").click();
		} else {// 未指定参数
			getUiObjectById("button2").click();
			if (userType == 0) {// 账号类型为手机号
				setGivenParams(getUiObjectById("phoneNo"), "+86" + userName);
				getUiObjectById("button1").click();
			}
			if (deviceInfo == null) {
				deviceInfo = new DeviceInfo();
			}
			deviceInfo.setAndroidId(getUiObjectById("android_id").getText()).setSSID(getUiObjectById("ssid").getText())
					.setBSSID(getUiObjectById("bssid").getText()).setPhoneNum(getUiObjectById("phoneNo").getText())
					.setICCID(getUiObjectById("simSerial").getText()).setIMSI(getUiObjectById("subscriberId").getText())
					.setSimStatus(getUiObjectById("simState").getText())
					.setOperatorId(getUiObjectById("operId").getText())
					.setOperatorName(getUiObjectById("operName").getText())
					.setCountryCode(getUiObjectById("isoCode").getText()).setModel(getUiObjectById("MODEL").getText())
					.setManufacture(getUiObjectById("MANUFACTURER").getText())
					.setHardware(getUiObjectById("HARDWARE").getText()).setBrand(getUiObjectById("BRAND").getText());
		}
	}

	private static UiObject getUiObjectById(String resourceId, String des) throws UiObjectNotFoundException {
		UiObject mEditText = new UiObject(new UiSelector().resourceId(GlobalConsts.PACKAGE_NAME + ":id/" + resourceId));
		mEditText.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
		setAssert(des + " not exists", mEditText.exists());
		return mEditText;
	}

	private static UiObject getUiObjectById(String resourceId) throws UiObjectNotFoundException {
		UiObject mEditText = new UiObject(
				new UiSelector().resourceId(GlobalConsts.EXPOSED_PACKAGE_NAME + ":id/" + resourceId));
		mEditText.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
		setAssert(resourceId + " not exits", mEditText.exists());
		return mEditText;
	}

	private static void setGivenParams(UiObject ui, String params) throws UiObjectNotFoundException {
		if (params == null || params.isEmpty())
			return;
		while (!"".equals(ui.getText())) {
			ui.clickBottomRight();
			for (int i = 0; i < ui.getText().length(); i++) {
				UiDevice.getInstance().pressDelete();
			}
		}
		ui.setText(Utf7ImeHelper.e(params));
	}

	/*
	 * 关闭IMEI修改器
	 **/
	private static void exitXposed() throws IOException {
		log("--exit Xposed app");
		while (true) {
			UiDevice.getInstance().pressBack();
			UiObject mLauncher = new UiObject(new UiSelector().packageName("com.microvirt.launcher"));
			if (mLauncher.exists()) {
				break;
			}
		}

	}

	/*
	 * 关闭IMEI修改器
	 **/
	// private static void exit008K() throws IOException {
	// log("--exit 008K app");
	// // UiDevice.getInstance().pressHome();
	// }

	/*
	 * 生成随机IMEI
	 **/
	private static void setRandomIMEI() throws IOException, UiObjectNotFoundException {
		log("step3:设置参数");
		launchXposed();
		setProperties();
		exitXposed();
	}

	/*
	 * 生成随机Imei
	 **/
	// private static void setRandomImei() throws IOException,
	// UiObjectNotFoundException {
	// log("step3:设置参数");
	// launch008K();
	// setProperty();
	// exit008K();
	// }

	/*
	 * 免费频道“今日限免书籍”单本书籍模拟阅读
	 **/
	private void readFreeBook(int index) throws UiObjectNotFoundException {
		if (index < 9) {
			log("web页面， 通过坐标查找“今日限免”书目");
			log("等待3s，以使“免费频道”web页面加载完成");
			SystemClock.sleep(GlobalConsts.WEB_PAGE_LOAD_WAIT_SECONDS);
			UiDevice.getInstance().click(POINT[index].x, POINT[index].y);
		} else {
			while (true) {
				// 查找“今日限免”书目
				log("原生页面，通过ID查找“今日限免”书目");
				if (index > 11) {
					index += 1;
				}
				UiObject mFreeBook = new UiObject(new UiSelector().index(index));
				if (mFreeBook.exists()) {
					mFreeBook.clickAndWaitForNewWindow();
					break;
				} else {
					log("未查询到可读书目，重新打开“免费频道页面”");
					UiDevice.getInstance().pressBack();
					checkUI();
				}
			}
		}
		UiObject mTitle = getUiObjectById("titlebar_text", "book details page");
		log("书籍详情匹配成功：" + mTitle.getText().trim());
		openBookByDrag(index);
		readBook(index);
		closeBookShelfDialog();
		// 返回到“书籍详情”，点击导航栏返回按钮，返回到“免费频道”
		UiObject mBackBtn = getUiObjectById("titlebar_level_2_back_button", "title bar back button");
		mBackBtn.clickAndWaitForNewWindow();
		// 返回到“免费频道”页面
		getUiObjectById("titlebar_text", "middle free channel page");
	}

	private void openBookByDrag(int index) {
		log("等待4s,以使书籍详情页面加载完成");
		SystemClock.sleep(4 * 1000L);
		UiDevice.getInstance().click(endPoint.x - 5, 440);
		SystemClock.sleep(1000L);
		UiDevice.getInstance().swipe(endPoint.x - 5, 440, 0, 440, 5);
	}

	private void readBook(int index) {
		// 模拟用户翻页开始阅读
		log("--Enter book page");
		UiObject mFrameView = new UiObject(new UiSelector().className("android.widget.FrameLayout"));
		mFrameView.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
		setAssert("book page not exits", mFrameView.exists());
		int num = (index < 9 ? index + 1 : index);
		log("开始阅读第" + num + "本书");
		for (int i = 0; i < GlobalConsts.PAGE_COUNT; i++) {
			SystemClock.sleep(GlobalConsts.PAGE_TURNING_TIME_INTERVAL);
			UiDevice.getInstance().pressKeyCode(25);
		}
		log("第" + num + "本书阅读结束");
	}

	// private void openBookByClick() throws UiObjectNotFoundException {
	// int pageType = judgePageType();
	// if (pageType == -1) {
	// log("web页面， 通过坐标查找“免费试读|继续阅读”按钮并点击");
	// log("等待5s，以使“书籍详情”web页面加载完成");
	// SystemClock.sleep(GlobalConsts.WEB_PAGE_LOAD_WAIT_SECONDS);
	// log(UiDevice.getInstance().getDisplayWidth() / 2 + ":" +
	// (UiDevice.getInstance().getDisplayHeight() - 30));
	// UiDevice.getInstance().click(UiDevice.getInstance().getDisplayWidth() / 2,
	// UiDevice.getInstance().getDisplayHeight() - 30);
	// } else {
	// // 进入“书籍详情”开始阅读 查找“免费试读|继续阅读”按钮并点击
	// log("原生页面,进入“书籍详情”开始阅读 查找“免费试读|继续阅读”按钮并点击");
	// UiObject mReadBtn = new UiObject(new
	// UiSelector().descriptionMatches("免费试读|继续阅读"));
	// mReadBtn.waitForExists(GlobalConsts.TIME_OUT_FOR_EXISTS);
	// setAssert("migu book details read button not exists", mReadBtn.exists());
	// mReadBtn.clickAndWaitForNewWindow();
	// }
	// }

	/*
	 * 关闭“加入书架”提示框
	 **/
	private static void closeBookShelfDialog() throws UiObjectNotFoundException {
		UiDevice.getInstance().pressBack();
		int count = 0;
		while (count++ < 2) {
			UiObject mCancelBtn = getUiObjectById("button_cancel", "book shelf dialog cancel button");
			if (mCancelBtn.exists()) {
				mCancelBtn.click();
				break;
			}
			UiDevice.getInstance().click(endPoint.x / 2, endPoint.y / 2);
		}

		if (count == 2) {
			setAssert("add to book shelf dialog closed failed ", true);
			throw new UiObjectNotFoundException("add to book shelf dialog closed failed");
		}
	}

	private static String getCurrentTime() {
		return DATE_FORMAT.format(new Date());
	}

	private static Long getNowTime() {
		return new Date().getTime();
	}

	private static void log(String message) {
		String out = String.format(FORMAT_LOG, getCurrentTime(), TAG, message);
		Log.d(TAG, out);
		PrintStream mPrintStream = null;
		try {
			mPrintStream = new PrintStream(System.out, true, "GB2312");
			mPrintStream.println(out);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}