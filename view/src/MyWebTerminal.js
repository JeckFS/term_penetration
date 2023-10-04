import { Terminal } from 'xterm'
import 'xterm/css/xterm.css'
//import { axios } from 'axios'
import React from 'react'
//import { AttachAddon } from "xterm-addon-attach";

class MyWebTerminal extends React.Component {
    constructor(props) {
        super(props)

        this.state = {
            hostList: ['hello world']
        }
        this.peer = ""
        this.refresh = this.refresh.bind(this)

        this.wsUri = "ws://127.0.0.1:8081/websocket" // 这里的IP和Port需要改成自己远程服务器的IP和Port
        this.term = new Terminal({
            fontFamily: 'Menlo, Monaco, "Courier New", monospace',
            fontWeight: 100,
            fontSize: 14,
            rows: 25,
        })

        this.ws = new WebSocket(this.wsUri)
        //this.term.loadAddon(new AttachAddon(this.ws));
        this.ws.onopen = (event) => {
            let msg = {
                cmd: 'REGISTRY',
                src: '我是前端',
            }
            this.ws.send(JSON.stringify(msg))
        }

        // window.ws = this.ws
        // window.onbeforeunload = function(e){     
        //     window.ws.close(1000)
        //     e = window.event||e;
        //     e.returnValue=("确定离开当前页面吗？");
        // } 

        this.ws.onmessage = (event) => {
            console.log('onmessage event:', event);
            let msg = JSON.parse(event.data);
            console.log('msg:', msg);
            if (msg.cmd === "TRANS") {
                this.term.write(msg.data)
            } else if (msg.cmd === 'LIST') {
                let data = JSON.parse(msg.data)
                console.log("data:", data);
                let hostList = []
                for (let i = 0; i < data.length; i++) {
                    const host = data[i];
                    hostList.push(<li key={i}><button onClick={(e) => {
                        this.ws.send(JSON.stringify({
                            cmd: 'CONNECTED', 
                            data: '消息转发',
                            dest: e.target.innerText,
                            src: '我是前端'
                        }))
                        this.peer = e.target.innerText
                    }} disabled={host === '我是前端'}>{host}</button></li>)
                }
                this.setState({
                    hostList: <ol>{hostList}</ol>,
                })
                console.log('hostList:',hostList);
            }
        }

        this.term.onData((key) => {
            console.log("onData:", key, key.charCodeAt(0));
            this.ws.send(JSON.stringify({
                cmd: 'TRANS',
                data: key,
                dest: this.peer,
                src: '我是前端',
            }))
        })
    }


    componentDidMount() {
        this.term.open(document.getElementById("terminal"))
        this.term.focus()
    }

    render() {
        return (
            <div>
                <button onClick={this.refresh}>刷新</button>
                <div id="hostList" style={{width: "50%", height: "50px", background: "yellow"}}>{this.state.hostList}</div>
                <div id="terminal"></div>
            </div>
        )
    }

    refresh(event){
        let data = {
            cmd: 'LIST',
        }
        this.ws.send(JSON.stringify(data))
    }
}

export default MyWebTerminal