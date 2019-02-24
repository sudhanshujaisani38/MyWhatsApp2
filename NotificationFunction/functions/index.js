'use strict'


const functions = require('firebase-functions');

const admin=require('firebase-admin');

admin.initializeApp(functions.config().firebase);


exports.sendNotification=functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite((change,context)=>{

  
const user_id=context.params.user_id;

  const notification = context.params.notification;


  console.log('We have a notification to send to: ',user_id);

  if(!change.after.val()){
  
  return console.log('A notification has been deleted from the database:',notification_id);
 
 }
  
const deviceToken=admin.database().ref(`/Users/`+user_id+`/token`).once('value');
 
 return deviceToken.then(result=>{
 
   const token_id=result.val();

    const payload={
    
  notification:{
        title:"Friend Request",
        body:"You have recieved a new Friend Request",
            }
    };
   
 return admin.messaging().sendToDevice(token_id,payload).then(response=>{
   
   console.log('Notification sent to',token_id);
    });

  });

});
