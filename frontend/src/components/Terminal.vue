<script setup>
import {onBeforeUnmount, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import {AttachAddon} from "xterm-addon-attach/src/AttachAddon";
import {Terminal} from "xterm";
import "xterm/css/xterm.css";

const props = defineProps({
    id: Number
})
const emits = defineEmits(['dispose'])

const terminalRef = ref()

const socket = new WebSocket(`ws://127.0.0.1:8080/terminal/${props.id}`)
socket.onclose = evt => {
    if(evt.code !== 1000) {
        ElMessage.warning(`Fail to connect: ${evt.reason}`)
    } else {
        ElMessage.success('Connection closed')
    }
    emits('dispose')
}
const attachAddon = new AttachAddon(socket);
const term = new Terminal({
    lineHeight: 1.2,
    rows: 20,
    fontSize: 13,
    fontFamily: "Monaco, Menlo, Consolas, 'Courier New', monospace",
    fontWeight: "bold",
    theme: {
        background: '#000000'
    },
    // cursor blick
    cursorBlink: true,
    cursorStyle: 'underline',
    scrollback: 100,
    tabStopWidth: 4,
});
term.loadAddon(attachAddon);

onMounted(() => {
    term.open(terminalRef.value)
    term.focus()
})

onBeforeUnmount(() => {
    socket.close()
    term.dispose()
})
</script>

<template>
    <div ref="terminalRef" class="xterm"/>
</template>

<style scoped>

</style>