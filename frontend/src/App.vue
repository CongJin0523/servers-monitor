<script setup>
import DarkModeToggle from "@/components/DarkModeToggle.vue";

// useDark({
//     selector: 'html',
//     attribute: 'class',
//     valueDark: 'dark',
//     valueLight: 'light',
//     }
// )
// useDark({
//     onChanged(dark) {useToggle(dark)}
// })
</script>

<template>
    <div>

        <router-view></router-view>
    </div>
</template>

<style scoped>

</style>
