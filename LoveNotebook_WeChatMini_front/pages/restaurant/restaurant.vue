<template>
	<view>
		<MainHeader title="恋爱餐厅"></MainHeader>

		<!-- 正文内容 -->
		<view class="content">

			<!-- header -->
			<view class="content-top">
				<view v-if="!isLove"
					style="font-size: 37rpx; color: #fff; width: 100%; text-align: center; margin-top: 100rpx;">
					请先寻找您爱的另一半</view>

				<view class="content-top-main" v-if="isLove">
					<view class="left">
						<u-avatar :src="user.userAvatar" size="large"></u-avatar>
						<view>{{user.userNickName}}</view>
					</view>
					<view class="left-identity" v-if="isRestaurant">{{leftIdentity}}</view>

					<view class="middle" @click="gotoEditNamePage()">
						{{restaurant.restaurantName}}
						<image src="../../static/img/pen.png"></image>
					</view>

					<view class="middle-lv">
						<image :src=' "../../static/img/LV/LV" +  restaurant.restaurantGrade + ".png" '></image>
					</view>

					<view class="right">
						<u-avatar :src="user.lover.userAvatar" size="large"></u-avatar>
						<view>{{user.lover.userNickName}}</view>
					</view>
					<view class="right-identity" v-if="isRestaurant">{{rightIdentity}}</view>
				</view>
			</view>

			<!-- main -->
			<view class="content-main" :style="'height:'+clientHeight+'px;'">

				<scroll-view class='left-scroll' scroll-y style="height: 100%">

					<view class="food-category" v-for="(item,index) in foodCategoryList"
						@click="foodCategoryClick(index)">
						<view :class="item.foodCategoryIsCheck == true ? 'food-category-left-1' : 'food-category-left'">
						</view>
						<view
							:class="item.foodCategoryIsCheck == true ? 'food-category-right-1' : 'food-category-right'">
							{{item.foodCategoryName}}
						</view>
					</view>

				</scroll-view>

				<scroll-view class='right-scroll' scroll-y style="height: 100%">
					<view class="right-scroll-top">
						—<font>{{rightScrollTopTitle}}</font>—
					</view>

					<view style="width: 100%; height: 122rpx;"></view>

					<view v-for="(item,index) in foodList">
						<view class="right-scroll-main">
							<view class="right-scroll-main-item">
								<view class="right-scroll-main-img">
									<image :src="item.foodImg"></image>
								</view>

								<view class="right-scroll-main-r">
									<view class="right-scroll-main-name">
										{{item.foodName}}
									</view>
									<view class="right-scroll-main-count">
										已售{{item.foodSellCount}}
									</view>
									<view class="right-scroll-main-bottom">

										<view class="right-scroll-main-bottom-left">
											<image src="../../static/img/heart.png"></image>
											<view>{{item.foodHeartCount}}</view>
										</view>

										<view class="right-scroll-main-bottom-right" v-if="!contentBottom">
											<view class="minus" @click="foodCountMinus(index)">-</view>
											<view class="num">{{item.cartCount}}</view>
											<!-- <view class="num">{{getcartFoodCount(index)}}</view> -->
											<view class="add" @click="foodCountAdd(index)">+</view>
										</view>
									</view>
								</view>
							</view>
						</view>

						<view class="line" v-if="index == foodList.length - 1 ? false : true"></view>
					</view>
				</scroll-view>

			</view>

			<!-- footer -->
			<view class="content-bottom">

				<!-- 老板页面 -->
				<view class="content-bottom-boss" v-if="contentBottom">
					<!-- <view class="content-bottom-boss" v-if="false"> -->
					<view class="btn btn_category" @click="foodCategoryManage()">
						类别管理
					</view>
					<view class="btn btn_addfood" @click="addFood()">
						添加食品
					</view>
				</view>

				<!-- 顾客页面 -->
				<view class="content-bottom-customer" v-if="!contentBottom">
					<!-- <view class="content-bottom-customer" v-if="true"> -->
					<view class="cart" @click="cartClick()">
						<image src="../../static/img/cart.png"></image>
						<view class="cart-num">{{cartList.length}}</view>
					</view>

					<view class="count">
						<view>总计：</view>
						<image src="../../static/img/heart.png"></image>
						<font>{{cartFoodHeartCount}}</font>
					</view>

					<view class="btn btn_addfood" @click="gotoOrder()">
						去结算
					</view>
				</view>

			</view>

		</view>

		<!-- 未开店 遮罩层 -->
		<u-mask :show="maskShow" @click="maskShow = false">
			<view class="warp">
				<view class="rect" @tap.stop>
					<view class="thead">
						<view class="top">
							请选择您的的角色
						</view>
						<image @click="maskShow = false" src="../../static/img/close.png" class="close"></image>
					</view>
					<view class="text">
						<p>创建店铺前，请先选择您的角色</p>
						<view class="midd">
							<p>玩法说明：</p>
							<p>老板可以进行店铺类别及商品管理</p>
							<p>顾客可以对老板的店铺商品进行购买</p>
							<p>每人初始50爱心，用于消费，后期可赚取</p>
						</view>
						<p>当您的角色选定后，您的另一半将自动成为另一个角色</p>
					</view>

					<view class="check">
						<view>我要当老板，打理店铺</view>
						<image @click="maskCheckBoss()" :src='maskCheckBossUrl'></image>
					</view>

					<view class="check">
						<view>我要当顾客，买买买买</view>
						<image @click="maskCheckCustomer()" :src='maskCheckCustomerUrl'></image>
					</view>

					<view class="submit" @click="maskSubmit()">
						确定
					</view>

				</view>
			</view>
		</u-mask>

		<u-mask :show="cartMaskShow" @click="cartMaskShow = false"
			:custom-style="{background: 'rgba(0, 0, 0, 0.5)', height: cartMaskHeight * 1.1 + 'px'}">
			<view class="cart_warp">
				<view class="cart_rect" @tap.stop>
					<view class="cart-top">
						<view :class="cartTopSelectAllClass" @click="cartTopSelectAll()"></view>
						<view class="all">全选</view>
						<!-- <view class="text">删除</view> -->
						<view class="text" @click="clearAllCart()">清空购物车</view>
					</view>

					<scroll-view class='cart-scroll' scroll-y style="height: 500rpx">
						<view class="cart-scroll-item" v-for="(item,index) in cartList">
							<view :class="item.cartIsCheck == 0 ? 'btn' : ' btn checked' "
								@click="cartItemClick(index)"></view>

							<image :src="item.food.foodImg"></image>

							<view class="item-mid">
								<view class="item-mid-name">{{item.food.foodName}}</view>
								<view class="item-mid-sell">已售{{item.food.foodSellCount}}</view>
								<view class="item-mid-heart">
									<image src="../../static/img/heart.png"></image>
									<view>{{item.food.foodHeartCount}}</view>
								</view>
							</view>

							<view class="right-btn">
								<view class="minus" @click="foodCartCountMinus(index)">-</view>
								<view class="num">{{item.cartFoodCount}}</view>
								<view class="add" @click="foodCartCountAdd(index)">+</view>
							</view>
						</view>

					</scroll-view>


				</view>
			</view>
		</u-mask>

		<MyTabBar></MyTabBar>
	</view>
</template>

<script>
	import MainHeader from "../../components/main_header.vue"
	import MyTabBar from "../../components/my_tabBar.vue"
	import common from "../../utils/common"
	import storage from "../../utils/storage"

	export default {
		components: {
			MainHeader,
			MyTabBar
		},
		data() {
			return {
				// 解决android和ios底部适配问题
				clientHeight: 0,
				isLove: true,
				isRestaurant: false,
				rightIdentity: "",
				leftIdentity: "",
				rightScrollTopTitle: "",
				maskShow: false,
				maskCheckBossUrl: "../../static/img/res1.png",
				maskCheckCustomerUrl: "../../static/img/res0.png",
				contentBottom: true,
				cartMaskShow: false,
				cartMaskHeight: 0,
				cartIsAllCheck: 0,
				cartFoodHeartCount: "0",
				cartTopSelectAllClass: "btn checked",
				foodCategoryList: [{
						foodCategoryId: 1,
						foodCategoryName: "默认分类",
						restaurantId: 1,
						foodCategoryIsCheck: true
					},
					{
						foodCategoryId: 2,
						foodCategoryName: "好吃的菜",
						restaurantId: 1,
						foodCategoryIsCheck: false
					}
				],
				foodList: [],
				restaurant: {
					restaurantName: "这是个神秘的小店",
					restaurantGrade : 1
				},
				user: {},
				cartList: []
			};
		},
		onPullDownRefresh() {
			this.init();
			//刷新数据之后停止刷新效果
			uni.stopPullDownRefresh()
		},
		onLoad() {
			this.getClineHeight();
		},
		onShow() {
			this.init();
		},
		methods: {
			async init(){
				await this.$http.httpGetByToken('/user/beforeLogin').then(res => {
					storage.set("userinfo", res.data.data);
				})
				this.user = storage.get("userinfo");
				let b = this.checkIsLove();
				if (b) {
					// 已恋爱 再去检验是否开店
					await this.checkIsRestaurant();
					let restaurant = storage.get("restaurantInfo");
					if (restaurant != null && restaurant != undefined && restaurant != "") {
						await this.getfoodCategoryList();
						await this.getAllFood();
						// 当前用户是老板
						if (restaurant.restaurantBossId == this.user.userId) {
							await this.getCartList();
						} else {
							// 当前用户是顾客
							// 获取购物车信息--->getfoodList();
							await this.getCartList();
						}
					}
				}
				this.setRightScrollTopTitle();
			},
			getfoodList() {
				let foodCategoryId;
				let that = this;
				let restaurant = storage.get("restaurantInfo");
				if (this.foodList.length == 0) {
					for (let i = 0; i < this.foodCategoryList.length; i++) {
						if (this.foodCategoryList[i].foodCategoryIsCheck == true) {
							foodCategoryId = this.foodCategoryList[i].foodCategoryId
							let allFoodInfo = storage.get("allFoodInfo")

							for (let i = 0; i < allFoodInfo.length; i++) {
								if (allFoodInfo[i].foodCategoryId == foodCategoryId) {
									that.foodList.push(allFoodInfo[i]);
								}
							}
						}
					}
				}
			},
			async getfoodCategoryList() {
				let restaurant = storage.get("restaurantInfo");
				let that = this;
				await this.$http.httpGetByToken("/foodCategory/getFoodCategoryByRestaurantId/" + restaurant.restaurantId).then(
					res => {
						console.log(res);
						if (res.data.data != null && res.data.data != undefined && res.data.data != "" && res.data.data
							.length != 0) {
							that.foodCategoryList = res.data.data;
						}
					})
			},
			async getAllFood() {
				let restaurant = storage.get("restaurantInfo");
				await this.$http.httpGetByToken("/food/getFoodInfoByRestaurantId/" + restaurant.restaurantId).then(res => {
					console.log(res);
					storage.set("allFoodInfo", res.data.data);
				})
			},
			async getCartList() {
				let that = this;
				await this.$http.httpGetByToken("/cart/getCartInfoByUserId").then(res => {
					console.log(res);
					that.cartList = res.data.data
					that.cartFoodHeartCount = res.data.msg

					that.getfoodList()
					that.checkCartIsALLSelect()
				})

			},
			// getcartFoodCount(index) {
			// 	if (this.cartList.length == 0) {
			// 		return 0;
			// 	} else {
			// 		for (let i = 0; i < this.cartList.length; i++) {
			// 			if (this.foodList[ixdex].foodId == this.cartList[i].cartFoodId) {
			// 				return this.cartList[i].cartFoodCount;
			// 			} else {
			// 				return 0;
			// 			}
			// 		}
			// 	}

			// },
			// 校验购物车是否全选
			checkCartIsALLSelect() {
				let cartList = this.cartList
				let flag = true;
				for (let i = 0; i < cartList.length; i++) {
					if (cartList[i].cartIsCheck == 1) {
						flag = true;
					} else {
						flag = false;
						break;
					}
				}
				if (flag == true) {
					this.cartTopSelectAllClass = "btn checked";
					return true;
				} else {
					this.cartTopSelectAllClass = "btn";
					return false;
				}
			},
			// 购物车全选
			cartTopSelectAll() {
				let b = this.checkCartIsALLSelect();
				if (!b) {
					for (let i = 0; i < this.cartList.length; i++) {
						if (this.cartList[i].cartIsCheck == 0) {
							this.cartList[i].cartIsCheck = 1
						}
					}
					this.checkCartIsALLSelect();
					return;
				}
				if (b) {
					for (let i = 0; i < this.cartList.length; i++) {
						if (this.cartList[i].cartIsCheck == 1) {
							this.cartList[i].cartIsCheck = 0
						}
					}
					this.checkCartIsALLSelect();
					return;
				}

			},
			// 清空购物车
			clearAllCart() {
				let that = this;
				this.$http.httpPostByToken("/cart/deleteCart").then(res => {
					console.log(res);
					that.init();
					for (let i = 0; i < that.foodList.length; i++) {
						that.foodList[i].cartCount = 0;
					}
					that.cartMaskShow = false
				})
			},
			// 购物车商品选择
			cartItemClick(index) {
				if (this.cartList[index].cartIsCheck == 1) {
					this.cartList[index].cartIsCheck = 0
					let b = this.checkCartIsALLSelect();
					console.log(b)
					return;
				}
				if (this.cartList[index].cartIsCheck == 0) {
					this.cartList[index].cartIsCheck = 1
					this.checkCartIsALLSelect();
					return;
				}
			},
			// 购物袋单击
			cartClick() {
				if (this.cartList.length != 0) {
					this.cartMaskShow = true;
				}
			},
			// 校验当前用户是否恋爱
			checkIsLove() {
				if (this.user.userIsLove == 0) {
					this.isLove = false;
					return false;
				} else {
					this.isLove = true;
					return true;
				}
			},
			// 校验当前用户是否开店
			async checkIsRestaurant() {
				let that = this;
				await this.$http.httpGetByToken("/restaurant/checkUserIsCreateRestaurant").then(res => {
					console.log(res);
					if (res.data.flag == true) {
						storage.set("restaurantInfo", res.data.data)
						that.restaurant = storage.get("restaurantInfo");
						if (that.restaurant.restaurantBossId == that.user.userId) {
							that.leftIdentity = "老板"
							that.rightIdentity = "顾客"
							that.contentBottom = true;
						} else {
							that.leftIdentity = "顾客"
							that.rightIdentity = "老板"
							that.contentBottom = false;
						}
						that.isRestaurant = true;
					} else {
						that.maskShow = true
						that.isRestaurant = false
					}
				});
			},
			gotoOrder() {
				if (this.cartList.length == 0) {
					this.$u.toast(`购物车为空`);
					return;
				}
				storage.set("cartList", this.cartList);

				uni.navigateTo({
					url: "../restaurant_order/restaurant_order"
				})
			},
			// 食物-按钮
			foodCountMinus(index) {
				if (this.foodList[index].cartCount != 0) {
					let foodId = this.foodList[index].foodId
					let that = this;
					this.$http.httpPostByToken("/cart/cartMinus/" + foodId).then(res => {
						console.log(res);
						that.init();
						this.foodList[index].cartCount -= 1;
					})
				} else {
					return;
				}
			},
			// 食物+按钮
			foodCountAdd(index) {
				let foodId = this.foodList[index].foodId
				let that = this;
				this.$http.httpPostByToken("/cart/cartAdd/" + foodId).then(res => {
					console.log(res);
					that.init();
					this.foodList[index].cartCount += 1;
				})
			},
			// 购物车食物-按钮
			foodCartCountMinus(index) {
				let foodId = this.cartList[index].cartFoodId
				let that = this;
				this.$http.httpPostByToken("/cart/cartMinus/" + foodId).then(res => {
					console.log(res);
					that.init();
					for (let i = 0; i < that.foodList.length; i++) {
						if (that.foodList[i].foodId == foodId) {
							that.foodList[i].cartCount -= 1;
						}
					}
				})
			},
			// 购物车食物+按钮
			foodCartCountAdd(index) {
				let foodId = this.cartList[index].cartFoodId
				let that = this;
				this.$http.httpPostByToken("/cart/cartAdd/" + foodId).then(res => {
					console.log(res);
					that.init();
					for (let i = 0; i < that.foodList.length; i++) {
						if (that.foodList[i].foodId == foodId) {
							that.foodList[i].cartCount += 1;
						}
					}
				})
			},
			// 遮罩层选择老板
			maskCheckBoss() {
				this.maskCheckBossUrl = "../../static/img/res1.png"
				this.maskCheckCustomerUrl = "../../static/img/res0.png"
			},
			// 遮罩层选择顾客
			maskCheckCustomer() {
				this.maskCheckBossUrl = "../../static/img/res0.png"
				this.maskCheckCustomerUrl = "../../static/img/res1.png"
			},
			// 遮罩层确定
			maskSubmit() {
				let restaurant = {};
				let that = this;
				if (this.maskCheckBossUrl == "../../static/img/res1.png") {
					restaurant.restaurantBossId = this.user.userId;
					restaurant.restaurantCustomerId = this.user.lover.userId;
				} else {
					restaurant.restaurantBossId = this.user.lover.userId;;
					restaurant.restaurantCustomerId = this.user.userId
				}
				console.log(restaurant);
				this.$http.httpPostByToken("/restaurant/createRestaurant", restaurant).then(res => {
					if (res.data.flag == true) {
						that.init();
						that.maskShow = false;
					} else {
						uni.showToast({
							icon: 'none',
							title: '创建失败，稍后再试'
						})
					}
				});

			},
			// 获取右部分类标题
			setRightScrollTopTitle() {
				for (let i = 0; i < this.foodCategoryList.length; i++) {
					if (this.foodCategoryList[i].foodCategoryIsCheck == true) {
						this.rightScrollTopTitle = this.foodCategoryList[i].foodCategoryName;
					}
				}
			},
			// 食物类别单击事件
			foodCategoryClick(index) {
				for (let i = 0; i < this.foodCategoryList.length; i++) {
					if (this.foodCategoryList[i].foodCategoryIsCheck == true) {
						this.foodCategoryList[i].foodCategoryIsCheck = false;
					}
					this.foodCategoryList[index].foodCategoryIsCheck = true;
					this.setRightScrollTopTitle();

					if (this.foodList.length != 0) {
						this.foodList.length = 0;

						this.getfoodList();
					}

				}
			},
			// 跳转修改名字页面
			gotoEditNamePage() {
				let restaurant = storage.get("restaurantInfo");
				if (restaurant == null || restaurant == undefined || restaurant == "") {
					uni.showToast({
						icon: 'none',
						title: '还未开店'
					})
					return;
				}
				uni.navigateTo({
					url: "../restaurant_editRestaurantName/restaurant_editRestaurantName"
				})
			},
			// 类别管理按钮
			foodCategoryManage() {
				let b = this.checkIsLove();
				if (!b) {
					uni.showToast({
						icon: 'none',
						title: '还没有伴侣'
					})
					return;
				}
				let restaurant = storage.get("restaurantInfo");
				if (restaurant == null || restaurant == undefined || restaurant == "") {
					uni.showToast({
						icon: 'none',
						title: '还未开店'
					})
					return;
				}
				uni.navigateTo({
					url: "../restaurant_foodCategoryManage/restaurant_foodCategoryManage"
				})
			},
			// 添加食品
			addFood() {
				let b = this.checkIsLove();
				if (!b) {
					uni.showToast({
						icon: 'none',
						title: '还没有伴侣'
					})
					return;
				}
				let restaurant = storage.get("restaurantInfo");
				if (restaurant == null || restaurant == undefined || restaurant == "") {
					uni.showToast({
						icon: 'none',
						title: '还未开店'
					})
					return;
				}
				uni.navigateTo({
					url: "../restaurant_addFood/restaurant_addFood"
				})
			},
			// 解决android和ios底部适配问题
			getBarHeight() {
				const res = uni.getSystemInfoSync()
				if (res.platform === 'ios') {
					return 44 + res.statusBarHeight + 50 + 34
				} else if (res.platform === 'android') {
					return 48 + res.statusBarHeight + 50
				} else {
					return 49 + res.statusBarHeight
				}
			},
			getcartMaskHeight() {
				const res = uni.getSystemInfoSync()
				if (res.platform === 'ios') {
					return 44 + res.statusBarHeight + 50 + 5
				} else if (res.platform === 'android') {
					return 48 + res.statusBarHeight + 50
				} else {
					return 63
				}
			},
			//获取可视区域高度
			getClineHeight() {
				let that = this;
				const res = uni.getSystemInfo({
					success: (res => {
						that.clientHeight = res.windowHeight - common.rpxTopx(380) - that.getBarHeight();
						that.cartMaskHeight = res.windowHeight - common.rpxTopx(120) - that
							.getcartMaskHeight()
						console.log()
					})
				});
			}
		}
	}
</script>

<style lang="scss" scoped src="./restaurant.scss"></style>