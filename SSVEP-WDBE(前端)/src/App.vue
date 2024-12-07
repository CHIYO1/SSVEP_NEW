<script setup>
import { provide } from "vue";

//引入echarts
import * as echarts from "echarts";

//通过provide提供echarts
provide("echarts", echarts);

</script>

<template>
  <div class="root">
    <router-view></router-view>
  </div>
</template>

<style>
.root {
  height: 100%;
  width: 100%;
}

body {
  background-color: #fafafa;
}
</style>