<script setup>
import Message from './Message.vue'
import { inject } from 'vue';

let store = inject("storeProvider",{});

let messages = [
  // {
  //   message: 'Hello',
  //   sender: 'Adel Shakal',
  //   time: '12:00',
  //   isSent: true,
  //   isReceived: true,
  //   isRead: true
  // },
  // {
  //   message: 'Hi',
  //   sender: 'John Doe',
  //   time: '12:01',
  //   isSent: true,
  //   isReceived: true,
  //   isRead: true
  // },
  // {
  //   message: 'How are you?',
  //   sender: 'Adel Shakal',
  //   time: '12:02',
  //   isSent: true,
  //   isReceived: true,
  //   isRead: true
  // },
  // {
  //   message: 'I am fine',
  //   sender: 'John Doe',
  //   time: '12:03',
  //   isSent: true,
  //   isReceived: true,
  //   isRead: true
  // },
  // {
  //   message: 'What about you?',
  //   sender: 'John Doe',
  //   time: '12:04',
  //   isSent: true,
  //   isReceived: true,
  //   isRead: true
  // },
  // {
  //   message: 'I am fine too',
  //   sender: 'Adel Shakal',
  //   time: '12:05',
  //   isSent: true,
  //   isReceived: true,
  //   isRead: true
  // }
]
let me = 'Adel Shakal'
</script>
<template lang="pug">
.chat-body.scrollable
 Message(v-for="message in (store.state.openedChat==null ? [] : Object.values(store.state.openedChat.messages) )" :key="message.time" :id="message.id" :reaction="message.reaction" :download="message.download" :downloadName="message.downloadName" :chat="message.chatId" :type="message.type " v-bind="message"  :sender="message.sender" )
</template>

