<script setup>
import { useDark, useToggle } from "@vueuse/core";
import { Sunny, Moon } from "@element-plus/icons-vue";
// Enable dark mode detection on <html> tag
const isDark = useDark({
    selector: 'html',
    attribute: 'class',
    valueDark: 'dark',
    valueLight: 'light',
});

// Function to toggle dark mode
const toggleDark = useToggle(isDark);
</script>

<template>
    <div class="dark-mode-toggle">
        <el-button @click="toggleDark()" circle >
            <el-icon :size="20" >
                <component :is="isDark ? Moon : Sunny" />
            </el-icon>
        </el-button>
    </div>
</template>

<style scoped>

/* Optional: Ensure smooth transition */
html {
    transition: background-color 0.3s ease, color 0.3s ease;
}
</style>