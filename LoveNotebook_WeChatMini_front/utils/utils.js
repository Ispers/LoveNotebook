/**
 * 给定日期如：'2023-05-12'  距离今天天数。
 */
export function daysDistanceToToday(date) {
	var date1 = Date.parse(date);
	var date2 = Date.parse(today());
	var ms = Math.abs(date2 - date1);
	var days = Math.floor(ms / (24 * 3600 * 1000));
	if (days == 0) {
		return days + 1;
	}
	return days;
}

/**
 * 生成 [ min, max ] 范围内的随机整数（大于等于min，小于等于max）
 */
export function getRandomNumber(min, max) {
	return Math.floor(Math.random() * (max - min + 1)) + min;
}

/**
 * 获取当前日期 '2023-05-12'
 */
export function today() {
	var today = new Date();
	var str = "";
	str += today.getFullYear() + "-";
	var month = today.getMonth() + 1; //返回值是 0（一月） 到 11（十二月） 之间的一个整数。
	if (month < 10) {
		str += "0";
	}
	str += month + "-";
	var day = today.getDate(); //返回值是 1 ~ 31 之间的一个整数
	if (day < 10) {
		str += "0";
	}
	str += day;
	return str;
}

/**
 * 显示消息提示框
 * @param {String} icon
 * @param {String} content
 * @param {number} time
 * @param {Function} successFuc
 */
export function showToast(icon, content, time = 1500,successFuc) {
	uni.showToast({
		icon: icon,
		title: content,
		duration: time,
		success: successFuc
	});
}

/**
 * 显示 loading 提示框, 需主动调用 uni.hideLoading 才能关闭提示框。
 */
export function showLoading(content = "加载数据中...", mask = true) {
	uni.showLoading({
		title: content,
		mask: mask
	});
}

export function hideLoading(timer = 0) {
	if (timer > 0) {
		var t = setTimeout(function() {
			uni.hideLoading();
			clearTimeout(t);
		}, timer);
	} else {
		uni.hideLoading();
	}
}

export function in_array(search, array) {
	let flag = false;
	for (let i in array) {
		if (array[i] == search) {
			flag = true;
			break;
		}
	}

	return flag;
}

export function isDataType(data, type) {
	return Object.prototype.toString.call(data) === '[object ' + type + ']';
}

export function ltrim(str, char) {
	let pos = str.indexOf(char);
	let sonstr = str.substr(pos + 1);
	return sonstr;
}

export function rtrim(str, char) {
	let pos = str.lastIndexOf(char);
	let sonstr = str.substr(0, pos);
	return sonstr;
}

/**
 * 保留当前页面，跳转到应用内的某个页面，使用uni.navigateBack可以返回到原页面。
 */
export function navigateTo(url, params) {
	uni.navigateTo({
		url: parseUrl(url, params)
	})
}

/**
 * 关闭当前页面，跳转到应用内的某个页面。
 */
export function redirectTo(url, params) {
	uni.redirectTo({
		url: parseUrl(url, params)
	});
}

/**
 * 关闭所有页面，打开到应用内的某个页面。
 */
export function reLaunch(url, params) {
	uni.reLaunch({
		url: parseUrl(url, params)
	});
}

/**
 * 跳转到 tabBar 页面，并关闭其他所有非 tabBar 页面。
 */
export function switchTab(url, params) {
	uni.switchTab({
		url: parseUrl(url, params)
	});
}

/**
 * 关闭当前页面，返回上一页面或多级页面
 */
export function navigateBack(delta) {
	uni.navigateBack({
		delta: delta
	});
}

/**
 * 预加载页面，是一种性能优化技术。被预载的页面，在打开时速度更快。
 */
export function preloadPage(url, params) {
	uni.preloadPage({
		url: parseUrl(url, params)
	});
}

export function prePage() {
	let pages = getCurrentPages();
	let prePage = pages[pages.length - 2];
	// #ifdef H5
	return prePage;
	// #endif
	return prePage.$vm;
}

/**
 * rpx转px
 * @param int|float num
 */
export function rpx2px(num) {
	// const info = uni.getSystemInfoSync()
	// let scale = 750 / info.windowWidth;
	// return (Number.isNaN(parseFloat(num)) ? 0 : parseFloat(num)) / scale;
	return uni.upx2px(num);
}

/**
 * @param int|float num
 */
export function px2rpx(num) {
	return num / (uni.upx2px(num) / num);
}

/**
 * 获取窗口的宽高
 */
export function getSystemInfo() {
	const info = uni.getSystemInfoSync();
	return {
		w: info.windowWidth,
		h: info.windowHeight
	};
}

/**
 * 功能：路由解析并且拼接
 * 使用：parseUrl('home', {id:1,type: 'add'})
 * 得到：'/pages/home?id=1&type=add'
 */
function parseUrl(url, params) {
	let arr = [];
	let string = '';
	for (let i in params) {
		arr.push(i + "=" + params[i]);
	}

	string = "/pages/" + url;
	if (arr.length > 0) {
		string += "?" + arr.join("&");
	}

	return string;
}