import styles from "./Note.module.css";
import React, {useState} from "react";
function Note() {
    const [notes, setNotes] = useState([])
    const [note, setNote] = useState("")
    const handleNoteChange = (e) => {
        e.preventDefault();
        setNote(e.target.value)
    }
    const addNotes = (e) => {
        e.preventDefault();
        if(e.target.value){
            setNotes([...notes, e.target.value])
            setNote("")
        }
    }
    const delNotes = (e) => {
        e.preventDefault();
        notes.splice(e.target.attributes.value.value*1,1)
        setNotes([...notes])
    }
    return (
        <div className={styles.container}>
            <header>
                <div>
                    <textarea unselectable="on" className={styles.content} placeholder="新增便利贴" value={note} onChange={handleNoteChange} onBlur={addNotes}/>
                </div>
            </header>
            <main>
                <div className={styles.messages}>
                    <ul>
                        {notes.map((note, index) => (
                            <li key={index}>{note}&nbsp;&nbsp;&nbsp;&nbsp;<a href="#" value={index} onClick={delNotes}>清除</a></li>
                        ))
                        }
                    </ul>
                </div>
            </main>
        </div>
    )
}
export default Note;