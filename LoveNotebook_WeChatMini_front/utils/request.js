import config from "@/config";  // 配置文件
import storage from "./storage"; // 缓存封装

export default {
	console(options){
		if(config.debug){
			// console.log("<<===============================================>>");
			// console.log("request start");
			// console.log("header" + JSON.stringify(options.header));
			// console.log("method: " + options.method + " URL: " + options.url);
			// console.log(options.data);
			// console.log("request end");
			// console.log("<<===============================================>>");
		} 
	},
	domain(){
		return config.uni_app_web_api_url.replace("api","");
	},
	sendByToken(options={}){
        // loading加载
        uni.showLoading({
           title: '加载中'
        });
        
        // 拼接路劲，下面的配置文件会提到
		options.url = config.uni_app_web_api_url + '' + options.url;
        // 请求方式
		options.method = options.method || "GET";
		
        // 这里看项目的情况来定，如果是没有token，那就删除这里，上面的storage也不需要引入
		let token = storage.getToken();
		if(token != null){
			options.header = { "token" : token };
		}
		
		this.console(options);  // 打印请求数据，调试用，上线可以注释
        
        // 发起Promise请求
		return new Promise((resolve, reject) =>{
			uni.request(options).then(data=>{
				var [error, res] = data;
				if(error != null){
					reject(error);
				}else{
					uni.hideLoading();
					resolve(res); 
				}
			});
		});
	},
	send(options={}){
	    // loading加载
	    uni.showLoading({
	       title: '加载中'
	    });
	    
	    // 拼接路劲，下面的配置文件会提到
		options.url = config.uni_app_web_api_url + '' + options.url;
	    // 请求方式
		options.method = options.method || "GET";
		
		this.console(options);  // 打印请求数据，调试用，上线可以注释
	    
	    // 发起Promise请求
		return new Promise((resolve, reject) =>{
			uni.request(options).then(data=>{
				var [error, res] = data;
				if(error != null){
					uni.hideLoading();
					reject(error);
				}else{
					uni.hideLoading();
					resolve(res); 
				}
			});
		});
	},
	get(url="",data={}){
		return this.send({
			url: url,
			data: data
		});
	},
	post(url="",data={}){
		return this.send({
			url: url,
			data: data,
			method: "POST"
		});
	},
	getByToken(url="",data={}){
		return this.sendByToken({
			url: url,
			data: data
		});
	},
	postByToken(url="",data={}){
		return this.sendByToken({
			url: url,
			data: data,
			method: "POST"
		});
	}
};
