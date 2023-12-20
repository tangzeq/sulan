import React from 'react';
import styles from './Platform.module.css'
import Index from "../index";
import Server from "../server/Server";
import Person from "../person/Person";
import Note from "../note/Note";
import ChatRoom from "../chatRoom/ChatRoom";
import File from "../file/File";
import Broadcast from "../broadcast/Broadcast";
import User from "../tables/User";
function Platform () {
    const chatRoom = () => {
        Index.root.render(
            <React.StrictMode>
                <button className={styles.close} onClick={platform}>🔠</button>
                <ChatRoom />
            </React.StrictMode>
        );
    }
    const server = () => {
        Index.root.render(
            <React.StrictMode>
                <button className={styles.close} onClick={platform}>🔠</button>
                <Server />
            </React.StrictMode>
        );
    }

    const person = () => {
        Index.root.render(
            <React.StrictMode>
                <button className={styles.close} onClick={platform}>🔠</button>
                <Person />
            </React.StrictMode>
        );
    }
    const note = () => {
        Index.root.render(
            <React.StrictMode>
                <button className={styles.close} onClick={platform}>🔠</button>
                <Note />
            </React.StrictMode>
        );
    }
    const platform = () => {
        Index.root.render(
            <React.StrictMode>
                <Platform />
            </React.StrictMode>
        );
    }
    const file = () => {
        Index.root.render(
            <React.StrictMode>
                <button className={styles.close} onClick={platform}>🔠</button>
                <File />
            </React.StrictMode>
        );
    }
    const broadcast = () => {
        Index.root.render(
            <React.StrictMode>
                <button className={styles.close} onClick={platform}>🔠</button>
                <Broadcast />
            </React.StrictMode>
        );
    }
    const user = () => {
      Index.root.render(
          <React.StrictMode>
              <button className={styles.close} onClick={platform}>🔠</button>
              <User />
          </React.StrictMode>
      )
    }
    return (
        <div className={styles.platform}>
            <div className={styles.card}>
                <div className={styles.plan}>
                <div className={styles.node} onClick={chatRoom}>
                    <img title="在线聊天" alt="在线聊天" src="../static/ico/chatRoom.svg"/>
                </div>
                <h5>聊天</h5>
                </div>
            </div>
            <div className={styles.card}>
                <div className={styles.plan}>
                <div className={styles.node} onClick={server}>
                    <img title="服务器" alt="服务器" src="../static/ico/server.svg"/>
                </div>
                <h5>服务</h5>
                </div>
            </div>
            <div className={styles.card}>
                <div className={styles.plan}>
                <div className={styles.node} onClick={person}>
                    <img title="个人信息" alt="个人信息" src="../static/ico/person.svg"/>
                </div>
                <h5>用户</h5>
                </div>
            </div>
            <div className={styles.card}>
                <div className={styles.plan}>
                <div className={styles.node} onClick={note}>
                    <img title="便签" alt="便签" src="../static/ico/note.svg"/>
                </div>
                <h5>便签</h5>
                </div>
            </div>
            <div className={styles.card}>
                <div className={styles.plan}>
                    <div className={styles.node} onClick={file}>
                        <img title="文件" alt="文件" src="../static/ico/file.png"/>
                    </div>
                    <h5>文件</h5>
                </div>
            </div>
            <div className={styles.card}>
                <div className={styles.plan}>
                    <div className={styles.node} onClick={broadcast}>
                        <img title="视频" alt="视频" src="../static/ico/file.png"/>
                    </div>
                    <h5>视频</h5>
                </div>
            </div>
            <div className={styles.card}>
                <div className={styles.plan}>
                    <div className={styles.node} onClick={user}>
                        <img title="用户" alt="用户" src="../static/ico/file.png"/>
                    </div>
                    <h5>用户</h5>
                </div>
            </div>
        </div>
    )
}
export default Platform;