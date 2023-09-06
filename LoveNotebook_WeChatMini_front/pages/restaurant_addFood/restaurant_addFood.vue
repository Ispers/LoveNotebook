<template>
	<view>
		<BackHeader title="添加商品"></BackHeader>
		<view class="content">

			<view class="img">
				<view class="img-text">
					<view class="img-text1">*</view>
					<view class="img-text2">商品封面图</view>
				</view>
				<image :src="food.foodImg" @click="checkImg()"></image>
			</view>

			<view class="name">
				<view class="text">
					<view class="text1">*</view>
					<view class="text2">商品名称</view>
				</view>
				<input v-model="food.foodName" type="text" :adjust-position="false" placeholder="请输入商品名称">
			</view>

			<view class="price">
				<view class="text">
					<view class="text1">*</view>
					<view class="text2">商品价格</view>
				</view>
				<input v-model="foodHeartCount" disabled type="text" @click="priceClick()" :adjust-position="false"
					placeholder="请输入商品售价">
			</view>

			<view class="cate">
				<view class="text">
					<view class="text1">*</view>
					<view class="text2">商品分类</view>
				</view>
				<input v-model="foodCategoryName" disabled type="select" :border="true" :adjust-position="false"
					@click="cateClick()" placeholder="请选择类别">
				<u-action-sheet :list="actionSheetList" v-model="sheetshow"
					@click="actionSheetCallback"></u-action-sheet>
			</view>

			<view class="add-btn" @click="addbtn()">确定</view>
		</view>



		<u-keyboard mode="number" @change="valChange" :mask="false" @backspace="backspace"
			v-model="keyboardshow"></u-keyboard>
	</view>
</template>

<script>
	import BackHeader from "../../components/back_header.vue"
	import storage from "../../utils/storage";
	import config from "@/config";

	export default {
		components: {
			BackHeader
		},
		data() {
			return {
				keyboardshow: false,
				food: {
					foodName: "",
					foodImg: "../../static/img/cinema.png",
					foodHeartCount: 0,
					foodCategoryId: 0,
					restaurantId: 0
				},
				foodHeartCount: "1",
				foodCategoryName: "",
				foodCategoryList: [],
				sheetshow: false,
				actionSheetList: [],
			};
		},
		onLoad() {
			this.init();
		},
		created() {
			// 监听从裁剪页发布的事件，获得裁剪结果
			var self = this;
			uni.$on('uAvatarCropper', path => {
				self.food.foodImg = path;
				// 可以在此上传到服务端
				uni.uploadFile({
					url: config.uni_app_web_api_url + '/upload/file',
					filePath: path,
					name: 'file',
					header: {
						"Content-Type": "multipart/form-data",
						"token": storage.getToken()
					},
					success: (e) => {
						console.log(e);
						self.food.foodImg = config.uni_app_web_url + e.data
						//写数据库
					},
					fail: (error) => {
						uni.showToast({
							title: error,
							duration: 2000
						});
					},
					complete: () => {
						uni.hideLoading();
					}
				});
			})
		},
		methods: {
			init() {
				var restaurant = storage.get("restaurantInfo");
				var that = this;
				this.$http.httpGetByToken("/foodCategory/getFoodCategoryByRestaurantId/" + restaurant.restaurantId).then(
					res => {
						console.log(res);
						that.foodCategoryList = res.data.data;

						var foodCategoryList = that.foodCategoryList;
						for (var i = 0; i < foodCategoryList.length; i++) {
							that.actionSheetList.push({
								text: foodCategoryList[i].foodCategoryName,
								foodCategoryId: foodCategoryList[i].foodCategoryId
							})
						}
					})
			},
			addbtn() {
				var that = this;
				var restaurant = storage.get("restaurantInfo");
				this.food.foodHeartCount = Number(this.foodHeartCount);
				this.food.restaurantId = restaurant.restaurantId;

				this.$http.httpPostByToken("/food/addFood", this.food).then(res => {
					console.log(res);
					if (res.data.flag == true) {
						that.$u.toast(`添加成功`);
						uni.navigateBack();
					}
				})
			},
			checkImg() {
				// 此为uView的跳转方法，详见"文档-JS"部分，也可以用uni的uni.navigateTo
				this.$u.route({
					// 关于此路径，请见下方"注意事项"
					url: '/pages/crop_picture/crop_picture',
					// 内部已设置以下默认参数值，可不传这些参数
					params: {
						// 输出图片宽度，高等于宽，单位px
						destWidth: 300,
						// 裁剪框宽度，高等于宽，单位px
						rectWidth: 300,
						// 输出的图片类型，如果'png'类型发现裁剪的图片太大，改成"jpg"即可
						fileType: 'jpg',
					}
				})
			},
			cateClick() {
				this.sheetshow = true
			},
			priceClick() {
				this.keyboardshow = true;
			},
			actionSheetCallback(index) {
				this.foodCategoryName = this.actionSheetList[index].text;
				this.food.foodCategoryId = this.actionSheetList[index].foodCategoryId
				console.log(this.actionSheetList[index].foodCategoryId)
			},
			// 按键被点击(点击退格键不会触发此事件)
			valChange(val) {
				// 将每次按键的值拼接到value变量中，注意+=写法
				this.foodHeartCount += val;
			},
			// 退格键被点击
			backspace() {
				// 删除value的最后一个字符
				if (this.foodHeartCount.length) this.foodHeartCount = this.foodHeartCount.substr(0, this.foodHeartCount
					.length - 1);
			}
		}
	}
</script>

<style lang="scss">
	.content {
		background-color: #fff;
	}

	.img {
		padding-left: 20rpx;

		.img-text {
			display: flex;
			padding-top: 15rpx;

			.img-text1 {
				color: #ffc0cb;
				font-size: 35rpx;
				margin-right: 15rpx;
			}

			.img-text2 {
				color: #000;
				font-size: 35rpx;
				font-weight: bold;
			}
		}

		image {
			margin-top: 20rpx;
			width: 180rpx;
			height: 180rpx;
			margin-bottom: 20rpx;
		}

		border-bottom: 1px solid #a0a0a0;
	}

	.text {
		display: flex;
		height: 80rpx;
		line-height: 80rpx;
		margin-right: 50rpx;

		.text1 {
			font-size: 35rpx;
			color: #ffc0cb;
			margin-right: 15rpx;
		}

		.text2 {
			font-size: 32rpx;
		}
	}

	.name {
		display: flex;
		padding-left: 20rpx;
		height: 80rpx;

		input {
			height: 80rpx;
		}

		border-bottom: 1px solid #f1f1f1;
	}

	.price {
		display: flex;
		padding-left: 20rpx;
		height: 80rpx;

		input {
			height: 80rpx;
		}

		border-bottom: 1px solid #f1f1f1;
	}

	.cate {
		display: flex;
		padding-left: 20rpx;
		height: 80rpx;

		input {
			height: 80rpx;
		}

		border-bottom: 1px solid #f1f1f1;
	}

	.add-btn {
		width: 100%;
		color: #fff;
		background-color: #ffc0cb;
		text-align: center;
		height: 80rpx;
		line-height: 80rpx;
		border-radius: 15rpx;
		margin-top: 100rpx;
	}
</style>