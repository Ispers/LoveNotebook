<template>
	<view>
		<u-navbar :is-back="false" :title="title" :background="background" title-color="#fff"
			:border-bottom="false"></u-navbar>
	</view>
</template>

<script>
	export default {
		props: ["title"],
		name: "main_header",
		data() {
			return {
				background: {
					// backgroundColor: '#D8BFD8',
					backgroundColor: '#ffc0cb',
					// 渐变色
					// backgroundImage: 'linear-gradient(45deg, #fc5c7d, #6a82fb)'
				}
			};
		}
	}
</script>

<style lang="scss"></style>