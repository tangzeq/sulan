import React, { useState,useEffect } from "react";
import styles from "./ChartRoom.module.css";
import Index from "../index";
import { EventSourcePolyfill } from 'event-source-polyfill';
const ChatRoom = () => {
    const [username, setUsername] = useState("");
    const [userId, setUserId] = useState("");
    const [messages, setMessages] = useState([]);
    const [inputValue, setInputValue] = useState("");
    let read = false;
    let flush = false;
    const handleInputChange = (e) => {
        setInputValue(e.target.value);
    };
    ChatRoom.setUsername = setUsername;
    ChatRoom.setUserId = setUserId;
    const changeScroll = () => {
        const textareas = document.getElementsByClassName(styles.content)
        flush = true;
        if(textareas.length>0) {
            for (let i in textareas) {
                textareas.item(i).style.height = "1px"
                textareas.item(i).style.height = textareas.item(i).scrollHeight+'px'
            }
            const element = document.getElementById('messages');
            element.scrollTop = element.scrollHeight;
            flush = false;
        }
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        if(!inputValue) return ;
        const param = {
            "message": inputValue
        };
        fetch(Index.address+'/message/sendMessage', {
            method: 'post',
            body: JSON.stringify(param),
            headers: {
                'Content-Type': 'application/json',
                'token': Index.token,
            }
        }).then(function(data) {
            setInputValue("");
        }).catch(err => {
            console.error(err)
            window.location.reload()
        })
    };
    const handleAISubmit = (e) => {
        e.preventDefault();
        if(!inputValue) return ;
        const param = {
            "message": inputValue
        };
        fetch(Index.address+'/message/sendChatGPT', {
            method: 'post',
            body: JSON.stringify(param),
            headers: {
                'Content-Type': 'application/json',
                'token': Index.token,
            }
        }).then(function(data) {
            setInputValue("");
        }).catch(err => {
            console.error(err)
            window.location.reload()
        })
    };
    useEffect(() => {
        const eventSource = new EventSourcePolyfill(Index.address+"/message/onLineMessage/1/0",{
            headers: {
                'token': Index.token
            }
        });
        eventSource.onmessage = (event) => {
            const message = JSON.parse(event.data);
            if(JSON.stringify(message) !== '{}' && message !==null && message.bs) {
                setMessages([...messages,message.bs])
                messages.push(message.bs)
                setUsername(Index.userName)
                setUserId(Index.userId)
                setTimeout(changeScroll,200);
            }
        };
        window.addEventListener('resize', changeScroll)
        return () => {
            eventSource.close()
            window.removeEventListener('resize', changeScroll)
        };
    }, []);
    return (
            <div className={styles.container_center}>
                <h1 className={styles.title}>Chat Room</h1>
                <div id = "messages" className={styles.messages}>
                    {messages.map((message, index) => (

                        <div
                            key={index} className={`${styles.message} ${
                            message.userId === userId ? styles.myMessage : styles.otherMessage
                        }`}
                        >
                            {message.userId !== userId &&<span className={styles.username}>{message.name}</span>}
                            <span className={styles.time}>{new Date(message.time).toLocaleString()}</span>
                            <textarea readOnly="readOnly" value={message.message} unselectable="on" className={styles.content}>{message.message}</textarea>
                        </div>
                    ))}
                </div>
                <form className={styles.form}>
                    <textarea
                        type="text"
                        value={inputValue}
                        onChange={handleInputChange}
                        className={styles.textarea}
                        placeholder="Type your message here"
                    />
                    <button type="submit" onClick={handleSubmit} className={styles.send}>
                        Send
                    </button>
                    <button type="submit" onClick={handleAISubmit} className={styles.ask}>
                        Ask
                    </button>
                </form>
            </div>
    );
};
export default ChatRoom;