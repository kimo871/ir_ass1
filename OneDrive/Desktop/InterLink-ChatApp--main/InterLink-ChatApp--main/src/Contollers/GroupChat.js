import GroupChat from '../models/GroupChat';
import { getAuth, createUserWithEmailAndPassword , signInWithEmailAndPassword , updateProfile } from "firebase/auth";
import {auth,db,store} from "../firebase/firebaseConfig"
import {getStorage,ref as storageRef,uploadBytes,getDownloadURL } from "firebase/storage"
import { ref , set , get , child , push,update,onValue,onChildChanged,off} from 'firebase/database';
import { debounce } from 'lodash';

class GroupChatController {
    constructor(store) {
        this.store = store;
        this.model = new GroupChat();
        this.chatsPath="groups";
        this.userChatsPath="groupChats";
    }

    async handleForm(e) {
         e.preventDefault()
          let formData = new FormData(e.target)
          let data = Object.fromEntries(formData.entries())
          let name = data.name;
          this.model.setName(name);
          const key = await this.model.save();
          let obj = {...this.store.state.openedChat};

          obj.chatId = key;

          this.store.state.openedChat = obj;

          this.store.state.openedChat = {
           userData : {name:this.model.name , photoURL:"../src/assets/icons/group.png"},
           messages:[],
           chatId:key,
        }

        console.log(this.store.state.openedChat)
    }

    async subscribeToGroups(userId){
           const groupsRef = ref(db,`groups`);
           const answer = await get(groupsRef);
           const userRef = ref(db,`users/${userId}`)
           const userChat = ref(db,`groupChats/${userId}`)
           let res ={};
           if(answer.exists()){
           Object.values(answer.val()).forEach(async(item)=>{
               res[item.chatId] = {unread:0,lastProcessedTime:0,lastReadTime:0}
           })

           await set(userChat,res);

        }
    }

    // =============================================================================================

    async getChatById(key) {
        const chat = this.chats[key];
        if (chat) {
            return chat.getData();
        }
        return null;
    }

    async updateChat(key, data) {
        const chat = this.chats[key];
        if (chat) {
            await chat.updateChat(chat.chatId, data);
            Object.assign(chat, data);
            return chat.getData();
        }
        return null;
    }

    generateKey(chatId, participants) {
        return `${chatId}_${participants[0].replace(/\./g, ',')}_${participants[1].replace(/\./g, ',')}`;
    }

    async getAllChats() {
        const allChatsData = [];
        for (const key in this.chats) {
            const chatData = await this.chats[key].getData();
            allChatsData.push(chatData);
        }
        return allChatsData;
    }

    async deleteChat(key) {
        const chat = this.chats[key];
        if (chat) {
            const chatRef1 = ref(db, `userChats/${chat.participants[0].replace(/\./g, ',')}/${key}`);
            const chatRef2 = ref(db, `userChats/${chat.participants[1].replace(/\./g, ',')}/${key}`);
            await set(chatRef1, null);
            await set(chatRef2, null);
            delete this.chats[key];
            return true;
        }
        return false;
    }

    async additionalDetails(answer,userEmail){
        return {name:answer.name , photoURL:"../src/assets/icons/group.png"};
}


 async sendMessage(msg,chatId){
    console.log("here.......",msg)
    try{
      await this.basic.createMessage(chatId,msg);
    }
    catch(err){
      console.log("error",err)
      state.value.feedback={
        status:400,
        msg:err.message
      }
    }
    finally{
      let chatDetails = this.store.state.recentChats;
      chatDetails.sort((a, b) => b.chatDetails.lastTime - a.chatDetails.lastTime );
     this.store.state.recentChats= [...chatDetails];
    }
  }
}

export default GroupChatController;