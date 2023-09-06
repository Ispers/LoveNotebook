<template>
	<view class="body">
		<BackHeader title="分类管理"></BackHeader>
		<view class="content">
			<view class="top">向左滑动删除</view>

			<scroll-view class='main' scroll-y>

				<u-swipe-action :show="item.show" :index="index" v-for="(item, index) in list"
					:key="item.foodCategoryId" @click="click" @open="open" :options="options">

					<view class="item" @click="itemClick(index)">
						<!-- <image mode="aspectFill" :src="item.images" /> -->
						<!-- 此层wrap在此为必写的，否则可能会出现标题定位错误 -->
						<!-- <view class="title-wrap">
							<text class="title u-line-2">{{ item.title }}</text>
						</view> -->
						{{item.foodCategoryName}}
					</view>

				</u-swipe-action>

			</scroll-view>

			<view class="add" @click="addshow = true">添加分类</view>
		</view>


		<u-popup v-model="addshow" mode="bottom" :height="addpopheight+'rpx'">
			<view class="padd">
				<view class="add-top">
					新增分类
				</view>

				<view class="add-main">
					<view>名称</view>
					<input type="text" v-model="addinput" :adjust-position="false" placeholder="请输入名称(6字以内)">
				</view>

				<view class="add-btn" @click="addbtn()">确定</view>
			</view>
		</u-popup>

		<u-popup v-model="updateshow" mode="bottom" :height="addpopheight+'rpx'">
			<view class="padd">
				<view class="add-top">
					修改分类
				</view>

				<view class="add-main">
					<view>名称</view>
					<input type="text" v-model="updateinput" :adjust-position="false" placeholder="请输入名称(6字以内)">
				</view>

				<view class="add-btn" @click="updatebtn()">确定</view>
			</view>
		</u-popup>

	</view>
</template>

<script>
	import BackHeader from "../../components/back_header.vue"
	import storage from "../../utils/storage";

	export default {
		components: {
			BackHeader
		},
		data() {
			return {
				addshow: false,
				updateshow: false,
				addpopheight: 700,
				addinput: "",
				updateinput: "",
				list: [],
				listitem: {},
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}]
			};
		},
		onLoad() {
			this.init();
			uni.onKeyboardHeightChange((res) => {
				console.log(res);
				// 监听软键盘的高度，页面隐藏后一定要取消监听键盘
				if (res.height == 0) {
					// 我这里仅考虑键盘的出现和隐藏
					this.addpopheight = 700
				} else {
					this.addpopheight = 700 + (res.height * 0.9)
				}
			})
		},
		onHide() {
			// 取消监听键盘高度
			uni.offKeyboardHeightChange((res) => {})
		},
		methods: {
			init() {
				var restaurant = storage.get("restaurantInfo");
				var that = this;
				this.$http.httpGetByToken("/foodCategory/getFoodCategoryByRestaurantId/" + restaurant.restaurantId).then(
					res => {
						console.log(res);
						that.list = res.data.data;
						for (var i = 0; i < that.list.length; i++) {
							that.list[i].show = false;
						}
					})
			},
			itemClick(index) {
				var that = this;
				this.updateinput = this.list[index].foodCategoryName

				this.listitem.foodCategoryId = this.list[index].foodCategoryId
				this.listitem.foodCategoryName = this.list[index].foodCategoryName
				this.listitem.restaurantId = this.list[index].restaurantId

				this.updateshow = true;
			},
			updatebtn() {
				var that = this;
				var restaurant = storage.get("restaurantInfo");

				this.listitem.foodCategoryName = this.updateinput;
				console.log(this.listitem)
				this.$http.httpPostByToken("/foodCategory/updateFoodCategory", this.listitem).then(res => {
					console.log(res);
					that.init();
					that.$u.toast(`修改成功`);
					that.updateshow = false
				})
			},
			addbtn() {
				var that = this;
				var restaurant = storage.get("restaurantInfo");
				this.$http.httpPostByToken("/foodCategory/addFoodCategory", {
					foodCategoryName: that.addinput,
					restaurantId: restaurant.restaurantId
				}).then(res => {
					console.log(res);
					that.init();
					that.$u.toast(`添加成功`);
					that.addshow = false
				})
			},
			click(index, index1) {
				var that = this;
				if (index1 == 0) {
					this.$http.httpPostByToken("/foodCategory/deleteFoodCategory/" + this.list[index].foodCategoryId).then(
						res => {
							console.log(res);
							that.init();
							this.$u.toast(`删除成功`);
						})
				}
			},

			// 如果打开一个的时候，不需要关闭其他，则无需实现本方法
			open(index) {
				// 先将正在被操作的swipeAction标记为打开状态，否则由于props的特性限制，
				// 原本为'false'，再次设置为'false'会无效
				this.list[index].show = true;
				this.list.map((val, idx) => {
					if (index != idx) this.list[idx].show = false;
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.body {
		// background-color: #fff;
		// height: 100%;
	}

	.top {
		width: 100%;
		height: 70rpx;
		line-height: 70rpx;
		text-align: center;
		color: #A0A0A0;
		border-bottom: 1px solid #bebebe;
	}

	.main {
		// border: 1px solid #000;
		height: 1100rpx;
	}

	.add {
		// border: 1px solid #000;
		background-color: #ffc0cb;
		color: #fff;
		width: 400rpx;
		height: 80rpx;
		line-height: 75rpx;
		border-radius: 40rpx;
		text-align: center;
		margin-left: 22%;
		margin-top: 30rpx;
	}

	.item {
		padding: 20rpx;
		text-align: left;
		font-size: 32rpx;
		height: 80rpx;
		color: $u-content-color;
		background-color: #f9f9f9;
		border-bottom: 1px solid #f1f1f1;
	}

	.padd {
		width: 80%;
		margin-left: 10%;
		margin-top: 5%;
	}

	.add-top {
		color: #000;
		font-size: 37rpx;
		font-weight: bold;
		margin-top: 15rpx;
		margin-bottom: 30rpx;
	}

	.add-main {
		display: flex;
		margin-top: 20rpx;
		margin-bottom: 50rpx;

		// border: 1px solid #000;
		view {
			// border: 1px solid #000;
			color: #A0A0A0;
			height: 70rpx;
			line-height: 70rpx;
			font-size: 32rpx;
			margin-right: 25rpx;
		}

		input {
			// border: 1px solid #000;
			height: 70rpx;
		}
	}

	.add-btn {
		width: 100%;
		color: #fff;
		background-color: #ffc0cb;
		text-align: center;
		height: 80rpx;
		line-height: 80rpx;
		border-radius: 15rpx;
	}
</style>