'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var assignBtn = document.querySelector('#assignBtn');
var disconnectBtn = document.querySelector('#disconnectBtn');

var stompClient = null;
var username = null;
var userType= 1;
var socket =null;
var privateTopic="/ge/topic";
var chatRoomId=null;
var connected=false;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    userType=document.querySelector("#clientType").value.trim();
    if (userType==1)
    {
        connectSocket();
    }else
    {
        chatRoomId=document.querySelector("#consoleId").value.trim();
        console.log(chatRoomId);
        connectSocket();
    }

    event.preventDefault();
}

function connectSocket(){
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        socket = new SockJS('/ge');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);

        socket.onclose=function(){
            if (connected)
            {
                //reconnect
                console.log('reconnect ... ');
                connected=false;
            }
        }

    }
}
function onConnected(frame) {

    console.log(socket);
    console.log(frame);

    connected=true;
    privateTopic+=chatRoomId;
    stompClient.subscribe(privateTopic, onMessageReceived);
    stompClient.subscribe("/user/queue/reply", function(message) {
        console.log(message.body);
    });
    var _content=userType==1?{}:{"chatRoomId":chatRoomId};
    stompClient.send(userType==1?'/oraclege/console.connect':'/app/ide.connect',
        {},
        JSON.stringify({sender: username, type: 'JOIN',content: JSON.stringify(_content)})
    );

    connectingElement.classList.add('hidden');
}

function onDisconnect(){
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };

        stompClient.send(privateTopic, {"side":userType}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function assign(event) {
     console.log('assign');
     $.ajax({
         url:"",
         success: function(msg){

         },
         error: function(msg){

         }

     })

}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
assignBtn.addEventListener("click",assign,true)
$('#disconnectBtn').click(function(){
      console.log('positive disconnect...')
      stompClient.disconnect(function() {
         connected=false;
      });
});