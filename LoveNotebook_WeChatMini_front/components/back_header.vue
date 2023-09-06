<template>
	<view>
		<u-navbar :is-back="true" :title="title" :background="background" title-color="#fff"
			:border-bottom="false"></u-navbar>
	</view>
</template>

<script>
	export default {
		props: ["title"],
		name: "back_header",
		data() {
			return {
				background: {
					backgroundColor: '#ffc0cb'
				}
			};
		}
	}
</script>

<style lang="scss"></style>