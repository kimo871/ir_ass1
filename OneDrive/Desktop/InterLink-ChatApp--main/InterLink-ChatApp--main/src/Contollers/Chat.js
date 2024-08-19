import DirectChat from '../models/DirectChat';
import Message from '../models/Message'
import DirectChatController from './DirectChat';
import GroupChatController from './GroupChat';
import { getAuth, createUserWithEmailAndPassword , signInWithEmailAndPassword , updateProfile } from "firebase/auth";
import {auth,db} from "../firebase/firebaseConfig"
import {getStorage,ref as storageRef,uploadBytes,getDownloadURL } from "firebase/storage"
import { ref , set , get , child , push,update,onValue,onChildChanged,off} from 'firebase/database';
import { debounce } from 'lodash';
class ChatController {
     strategies = new Map()
    constructor(strategy,store) {
        this.strategies.set('groups', GroupChatController);
        this.strategies.set('chats',  DirectChatController);
        this.store = store;
        this.setStrategy(strategy);    
    }

    setStrategy(strategy) {
        if (this.strategies.has(strategy)) {
            const StrategyClass = this.strategies.get(strategy);
            this.strategy = new StrategyClass(this.store);
            this.strategy.basic = this;
        } else {
            throw new Error(`Strategy ${strategy} not found`);
        }
    }

// =======================================================================================================

    async createChat(){
        
    }

//=====================================================================================================


    handleForm(e){}

//=====================================================================================================

    async updateChat(chatId,message){
        try{
          const chatRef = ref(db,`${this.strategy.chatsPath}/${chatId}`);
          await update(chatRef,{
            lastMessage:message.message,
            lastTime:message.time
          })
        const userChatRef = ref(db,`${this.strategy.userChatsPath}/${this.store.state.user.email.replace(/\./g, ',')}/${chatId}`)
        await update(userChatRef,{
          lastReadTime:message.time,
          lastProcessedTime : message.time
        })
          console.log(message.time)
          
        }
        catch(err){
          console.log(err);
        }
        finally{
          console.log("here update")
          
        }
    }


  //   async listenRecentMessages(chatId, messageSnapshot, chatDetails) {
  //     console.log("message added");
  //     const messages = messageSnapshot.val();
  //     console.log(messages);
  
  //     // If the chat is currently opened, update its messages
  //     if (this.store.state.openedChat?.chatId === chatId) {
  //         console.log("Updating opened chat messages");
  //         this.store.state.openedChat.messages = messages;
  //     }
  
  //     // Find the latest message
  //     const lastMessage = Object.values(messages)[Object.keys(messages).length - 1];
  //     console.log(lastMessage, "latest message");
  
  //     // Find the chat in the chatDetails array
  //     const existingChatIndex = chatDetails.findIndex(chat => chat.chatDetails.chatId === chatId);
  //     console.log(existingChatIndex);
  
  //     if (existingChatIndex !== -1) {
  //         const chat = chatDetails[existingChatIndex];
  //         const lastProcessedTime = chat.chatDetails.lastTime || 0;
  
  //         // Only update if the message is new
  //         if (lastMessage.time > lastProcessedTime) {
  //             const newMessages = Object.values(messages).filter(message => message.time > lastProcessedTime);
  //             console.log("New messages detected");
  
  //             // Update the chat details with the latest message and time
  //             chatDetails[existingChatIndex].chatDetails.lastMessage = lastMessage.message;
  //             chatDetails[existingChatIndex].chatDetails.lastTime = lastMessage.time;
  
  //             const userEmail = this.store.state.user.email.replace(/\./g, ',');
  
  //             // Adjust the unread count
  //             if (this.store.state.openedChat?.chatId !== chatId) {
  //                 // If the chat is not opened, increase unread count by the number of new messages
  //                 chatDetails[existingChatIndex].userDetails.unread = (chatDetails[existingChatIndex].userDetails.unread || 0) + newMessages.length;
  //             } else {
  //                 // If the chat is opened, reset the unread count
  //                 chatDetails[existingChatIndex].userDetails.unread = 0;
  //             }
  
  //             // Update the unread count and last message times in the database
  //             const readChat = ref(db, `${this.userChatsPath}/${userEmail}/${chatId}`);
  //             await update(readChat, {
  //                 unread: chatDetails[existingChatIndex].userDetails.unread,
  //                 lastProcessedTime: lastMessage.time
  //             });
  
  //             // Sort the recent chats based on the last message time
  //             chatDetails.sort((a, b) => b.chatDetails.lastTime - a.chatDetails.lastTime);
  //             this.store.state.recentChats = [...chatDetails];
  //             console.log(`Updated unread count for chatId ${chatId} to ${chatDetails[existingChatIndex].userDetails.unread}`);
  //         }
  //     }
  // }

  
// =======================================================================================================

async listenRecentMessages(chatId, messageSnapshot, chatDetails) {
  this.store.state.isUpdating = true;
    console.log("message added");
        const messages = messageSnapshot.val();
        console.log(messages);
        console.log(chatDetails[1]);
    
        // Get the latest message
        const lastMessage = Object.values(messages)[Object.keys(messages).length - 1];
        console.log(lastMessage, "latest message");
        console.log(chatId)
    
        // Find the index of the chat
        const existingChatIndex = chatDetails.findIndex(chat => chat.chatDetails.chatId == chatId);
        console.log(existingChatIndex);
    
        if (existingChatIndex !== -1 ) {
            const chat = chatDetails[existingChatIndex];
            const lastReadTime = chat.userDetails.lastReadTime || 0;
            const lastProcessedTime = chat.userDetails.lastProcessedTime || 0;

          
    
            // Only update if the message is new and hasn't been processed yet
             // Filter and process all messages that are newer than lastProcessedMessageTime
        const newMessages = Object.values(messages).filter(message => message.time > lastProcessedTime);

        if (newMessages.length > 0) {
            console.log("New messages detected:", newMessages);

            // Update the chat details with the latest message info
            const latestMessage = newMessages[newMessages.length - 1];
            console.log(latestMessage)
            chatDetails[existingChatIndex].chatDetails.lastMessage = latestMessage.message;
            chatDetails[existingChatIndex].chatDetails.lastTime = latestMessage.time;

            const userEmail = this.store.state.user.email.replace(/\./g, ',');

            if (this.store.state.openedChat?.chatId !== chatId) {
                // Increment unread count by the number of new messages
                chatDetails[existingChatIndex].userDetails.unread = (chatDetails[existingChatIndex].userDetails.unread || 0) + newMessages.length;
            } else {
                 this.store.state.openedChat.messages = messages;
                // Reset unread count when chat is opened
                chatDetails[existingChatIndex].userDetails.unread = 0;
                // Update last read time to the latest message time
                chatDetails[existingChatIndex].userDetails.lastReadTime = latestMessage.time;
            }

            // Update last processed message time to the latest message time
            chatDetails[existingChatIndex].userDetails.lastProcessedTime = latestMessage.time;

            
            chatDetails.sort((a, b) => b.chatDetails.lastTime- a.chatDetails.lastTime);
            this.store.state.recentChats = [...chatDetails];

            // Update unread count, last read time, and last processed message time in the database
            const readChat = ref(db, `${this.strategy.userChatsPath}/${userEmail}/${chatId}`);
            try {
                await update(readChat, { 
                    unread: chatDetails[existingChatIndex].userDetails.unread, 
                    lastReadTime: chatDetails[existingChatIndex].userDetails.lastReadTime,
                    lastProcessedTime: chatDetails[existingChatIndex].userDetails.lastProcessedTime
                });

                console.log(`Updated unread count for chatId ${chatId} to ${chatDetails[existingChatIndex].userDetails.unread}`);
            } catch (error) {
                console.error(`Failed to update unread count for chatId ${chatId}:`, error);
            }
        }
      }

      this.store.state.isUpdating = false;
    
};


// =======================================================================================================
    async getDetails(){

    }

//=====================================================================================================

    async fetchRecentChats(){
        try {
            this.store.state.loading.recentChats = true;
            console.log(this.store.state.user.email)
            this.store.state.recentChats = null;
            const userEmail = this.store.state.user.email.replace(/\./g, ',');
            const userChatsRef = ref(db, `${this.strategy.userChatsPath}/${userEmail}`);
    
            // Remove old listener if needed
            if (this.store.state.chatListener) {
                off(userChatsRef);  // Cleanup old listener if it exists
            }
    
            // Add new listener
            const chatListener = onValue(userChatsRef, async (snapshot) => {
            
                if (snapshot.exists()) {
                    const chatIds = Object.keys(snapshot.val());
                    console.log(chatIds)
                    let chatDetails = [];
                    console.log(this.strategy.userChatsPath,this.strategy.chatsPath)
                    // Use Promise.all to fetch all chat details in parallel
                    const fetchChatDetails = chatIds.map(async (chatId) => {
                        try {
                            const chatRef = ref(db, `${this.strategy.chatsPath}/${chatId}`);
                            const detailsSnapshot = await get(chatRef);
                            console.log(detailsSnapshot.val())
                            console.log("Constructed Path:", `${this.strategy.userChatsPath}/${userEmail}/${chatId}`);
    
                            const refd = ref(db, `${this.strategy.userChatsPath}/${userEmail}/${chatId}`);
                            const val = await get(refd);
    
                            if (!val.exists()) {
                                console.warn(`User details not found for chatId ${chatId}`);
                                return; // Skip this chatId if user details are missing
                            }
                            const info = val.val();
                            console.log("User Details:", info);
                            
    
                            if (detailsSnapshot.exists()) {
                                const answer = detailsSnapshot.val();
                                console.log(answer)
                                const additionalData = await this.strategy.additionalDetails(answer,userEmail);
                                // Set up a real-time listener for messages
                                console.log(additionalData)
                                const messageRef = ref(db, `messages/${chatId}`);
                                 onValue(messageRef, async (messageSnapshot) => {
                                    this.listenRecentMessages(chatId, messageSnapshot, chatDetails);
                                });
    
                                // chatdetails
                                // modeldetails
    
                                chatDetails.push({
                                  chatDetails:{
                                    chatId,
                                    lastMessage : answer.lastMessage,
                                    lastTime : answer.lastTime
                                  },
                                  userDetails:{
                                    ...additionalData,
                                    lastReadTime: info.lastReadTime || 0,
                                    lastProcessedTime : info.lastProcessedTime,
                                    unread: info.unread || 0
                                  }
                                  
                                });
                                console.log(chatDetails)
                            }
                        } catch (error) {
                           // console.error(`Error fetching details for chatId ${chatId}:`, error);
                        }
                    });

                    
                    // this.store.state.recentChats = chatDetails;
    
                    // Wait for all chat details to be fetched before updating state
                    await Promise.all(fetchChatDetails).then(()=>{
                        chatDetails.sort((a, b) => b.chatDetails.lastTime - a.chatDetails.lastTime);
                        console.log(chatDetails)
                       this.store.state.recentChats = chatDetails;
                    //  console.log(this.store.state.recentChats)
                    
                    })
    
                    // // Sort and update state
                    // chatDetails.sort((a, b) => b.lastTime - a.lastTime);
                    // this.store.state.recentChats = chatDetails;
                } else {
                    this.store.state.recentChats = []; // No chats found
                    
                }
            });
    
            // Store the listener reference in state for future cleanup
            this.store.state.chatListener = chatListener;
    
        } catch (error) {
            console.error('Error fetching recent chats:', error);
           this.store.state.recentChats = [];
           
        } finally {
           this.store.state.loading.recentChats = false
        }
    }

    //=====================================================================================================


    async createMessage(chatId,msg){
        try{
         // state.loading=true;

          console.log(msg)

          let result = msg;

          if(msg.type){
            result = await this.store.uploadFile("files",msg);
            
            console.log("upload url...",result)
          }

         
          let msgRef = ref(db,`messages/${chatId}`);

          let newMsgRef = push(msgRef);

          let domain =  import.meta.env.VITE_APP_FIREBASE_URL+"/v0/b/"+import.meta.env.VITE_APP_FIREBASE_STORAGE_BUCKET
          
          console.log(result,"ww",domain)

          const message = {
            id : newMsgRef.key,
            message : msg.type ? "Sent A file" : msg ,
            type : msg.type ?  "file" : "text",
            download : result,
            downloadName : msg.type ? msg.name : null,
            time : Date.now(),
            readBy:null,
            sender: {email:this.store.state.user.email,name:this.store.state.user.displayName,photoURL:this.store.state.user.photoURL}, // Example additional field
            chatId: chatId // Example additional field
          }

          await set(newMsgRef,message)

          message.sender = {name:this.store.state.user.displayName,photoURL:this.store.state.user.photoURL,email:this.store.state.user.email};

          console.log({[newMsgRef.key]:message})

          await this.updateChat(chatId,message);

          this.store.state.openedChat.messages = {...this.store.state.openedChat.messages , [newMsgRef.key]:message}
         
        }
        catch(err){
        console.log(err);
        }
        finally{
         // this.store.state.loading = false;
        }
    }

//=====================================================================================================

    async getMessages(chatId,userDetails){
        try{
          this.store.state.openedChat= null;
          this.store.state.loading.chatBody=true;
          let result= {};
          const chatRef = ref(db,`messages/${chatId}`);
          const dataRef = await get(chatRef);
          let messages = dataRef.val();
          if(messages){
          for ( let [key,item] of Object.entries(dataRef.val())){
            result[key]=item;
          }
        }

          const read1 = ref(db,`${this.strategy.userChatsPath}/${this.store.state.user.email.replace(/\./g, ',')}/${chatId}`);
           await update(read1,{unread:0});

          
          this.store.state.openedChat= {userData:userDetails,messages:result,chatId:chatId};
        }
        catch(err){
          console.log(err);
          //state.value.openedChat= {userData:userDetails,messages:[]};
        }
        finally{
          this.store.state.loading.chatBody=false;
        }
      }


    
      // ===============================================================================================
    async reactMessage(chatId,messageId,emoji){
      try{
       const msgRef = ref(db,`messages/${chatId}/${messageId}`);
       await update(msgRef,{
        reaction :  emoji
       })
         
       if (this.store.state.openedChat?.chatId === chatId) {
        const messages = this.store.state.openedChat.messages;
        Object.values(messages).forEach((item) => {
            if (item.id === messageId) {
                item["reaction"] = emoji;
            }
        });

        // Trigger a reactivity update by assigning the updated messages back to the store
        this.store.state.openedChat.messages = { ...messages };
    }
      }

      catch(err){
        console.log(err);
      }
    }
}

export default ChatController;