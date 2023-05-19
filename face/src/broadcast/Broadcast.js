import React, {useEffect, useRef, useState} from 'react';
import styles from "./Broadcast.module.css"
import Index from "../index";
import {EventSourcePolyfill} from "event-source-polyfill";
import HlsPlayer from 'react-hls-player';
const Broadcast = () => {
    //音视频录制
    const mediaRecorder = useRef(null);
    const [blobs, setBlobs] = useState([]);
    const recording = useRef(null);
    const streams = useRef(null)
    const interval = useRef(null)
    const initTV = () => {
        while (!streams.current){}
        mediaRecorder.current = new MediaRecorder(streams.current, {
            mimeType: 'video/webm; codecs=h264,vp9,opus',
            videoBitsPerSecond: 2e6,
            width: { min: 1920, ideal: 1920, max: 1920 },
            height: { min: 1080, ideal: 1080, max: 1080 },
            frameRate: {min:30, ideal: 30, max: 30 },
        });
        mediaRecorder.current.ondataavailable = (event) => {
            if (event.data) {
                blobs.push(event.data)
            }
        };
        mediaRecorder.current.onstop = () => {
            const formData = new FormData();
            let blob = new Blob(blobs, {type: "video/webm; codecs=h264,vp9,opus"})
            blobs.splice(0, blobs.length)
            const url = window.URL.createObjectURL(blob);
            const videoElement = document.createElement('video');
            videoElement.src = url;
            videoElement.preload = 'auto';
            videoElement.oncanplaythrough = () => {
                window.URL.revokeObjectURL(url)
                formData.append('file', blob);
                fetch(Index.address+'/broadcast/upload', {
                    method: 'POST',
                    body: formData,
                    headers: {
                        'token': Index.token,
                    }
                })
                if(recording.current !== null) mediaRecorder.current.start()
            }
            videoElement.onerror = () => {
                window.URL.revokeObjectURL(url)
                if(recording.current !== null) mediaRecorder.current.start()
            }
        }
    }
    const live = async () => {
        if(!navigator.mediaDevices) {
            alert("当前环境不支持,请尝试在google浏览器打开\n  1、访问chrome://flags/#unsafely-treat-insecure-origin-as-secure\n  2、加入系统访问地址"+window.location.href+" \n  3、设置enabled\n  4、点击浏览器右下角按钮重启浏览器\n  5、再次访问"+window.location.href)
            return
        }
        window.metv.style.display = 'block'
        recording.current = true
        streams.current = null
        if(interval.current) window.clearInterval(interval.current)
        interval.current = window.setInterval(() => {
            if (mediaRecorder.current && mediaRecorder.current.state === 'recording' && recording.current !== null) {
                mediaRecorder.current.stop()
            }
        }, 4000)
        streams.current = await navigator.mediaDevices
            .getUserMedia({
                audio: true,
                video: true,
            })
        initTV()
        document.getElementById("me").srcObject = streams.current;
        while (!mediaRecorder.current){} mediaRecorder.current.start()
    }
    //视讯列表
    const [broadcasts,setBroadcasts] = useState([])
    useEffect(() => {
        close()
        // flush()
        return () => {
            close()
        }
    },[])
    const flushInter = useRef(null);
    const flush = () => {
        if(flushInter.current) window.clearTimeout(flushInter.current)
        fetch(Index.address + '/broadcast/broadcasts', {
            headers: {
                'token': Index.token
            }
        })
            .then(res => res.json())
            .then(res => {
                broadcasts.splice(0,broadcasts.length)
                if(res && res.length>0) {
                    setBroadcasts([...broadcasts,...res])
                    res.map((e,i)=>{broadcasts.push(e)})
                }
                setBroadcasts([...broadcasts])
            }).finally(e => {
            // flushInter.current = window.setTimeout(flush,parseInt(Math.random()*1000)+1000)
        })
    }
    //音视频播放
    const eventSource = useRef(null)
    const [m3u8,setM3u8] = useState(null);
    const join = (e) => {
        e.preventDefault()
        window.showtv.style.display = 'block'
        if(eventSource.current) eventSource.current.close();
        eventSource.current = new EventSourcePolyfill(Index.address+"/broadcast/show/"+e.target.value,{
            headers: {
                'token': Index.token
            }
        });
        eventSource.current.onmessage = (event) => {
            const message = JSON.parse(event.data);
            if (JSON.stringify(message) !== '{}' && message !== null && message.file) {
                setM3u8(Index.address+"/broadcast/"+message.file.name)
            }
        };
    }
    const close = () => {
        if(window.metv) window.metv.style.display = 'none'
        if(window.showtv) window.showtv.style.display = 'none'
        recording.current = null
        if(eventSource.current) eventSource.current.close();
        eventSource.current = null
        if (streams.current) streams.current.getTracks().map((track, index) => track.stop())
        streams.current = null
        if(interval.current) window.clearInterval(interval.current)
        interval.current = null
        if(flushInter.current) window.clearTimeout(flushInter.current)
        setM3u8('')
        setM3u8(null)
    }
    return (
        <div className={styles.container_center} >
            <div className={styles.broadcasts}>
                <div id="metv" className={styles.me}>
                    <video id="me" autoPlay></video>
                </div>
                <div id="showtv" className={styles.she +" data-vjs-player"}>
                    {m3u8 && (<HlsPlayer
                        src={m3u8}
                        autoPlay={true}
                        controls={false}
                        className={styles.video_js}
                    />) }
                </div>
            </div>
            <div className={styles.broadcasts_list}>
                <div className={styles.broadcasts_list_empty}>
                    <button className={styles.file_join_button} onClick={live}>Live</button>
                    <button className={styles.file_join_button} onClick={close} >Close</button>
                    <button className={styles.file_join_button} onClick={flush} >Flush</button>
                </div>
                {broadcasts.length>0 && broadcasts.map((broadcast, index) => (
                    <div className={styles.broadcasts_list_item} key={index}>
                        <a id={broadcast.index}  title={new Date(broadcast.time).toLocaleString()}  href="#" target="_blank" rel="noopener noreferrer" className={styles.broadcast_link}>{broadcast.name}</a>
                        <button className={styles.file_join_button} name={broadcast.name} value={broadcast.userId} onClick={join} >Join</button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Broadcast;
