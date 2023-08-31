/*
*  使用方式 1 (只传入id)
	let verifyCode = new zVerify('verifyCode')

*  使用方式 2 (传入id, 配置项)
	let verifyCode = new zVerify('verifyCode', {
		num: 6,
		type: 'blend',
		strict: true
	})

*  验证
	let flag = verifyCode.validate(输入值)    //返回boolean

*  刷新
	verifyCode.refresh()
*/

!(function (window, document) {
    function zVerify(id, options) {
        //默认配置
        this.options = {
            type: 'number', //blend:数字字母、number:纯数字、letter:纯字母
            num: 4,  //长度
            isStrict: false, //是否区分大小写
            hasLine: true, //是否绘制干扰线
            hasDot: false, //是否绘制干扰点
            background: 'rgba(255, 255, 255, 0)', //背景颜色
            colorArr: [150, 220],   //字体颜色随机区间
            fontFamily: 'Microsoft Yahei Light',   //字体名称
            numArr: '2,3,4,5,6,7,8,9'.split(','),   //数字序列
            letterArr: 'a,b,d,e,f,g,h,i,j,m,n,q,r,t,y,A,B,D,E,F,G,H,J,L,M,N,Q,R,T,Y'.split(','),   //字母序列
        }
        Object.assign(this.options, options)
        this.options.id = id
        this.init()
        this.refresh()
    }

    zVerify.prototype = {
        /*初始化*/
        init: function () {
            let con = document.getElementById(this.options.id)
            let canvas = document.createElement('canvas')
            this.options.width = con.offsetWidth || 100
            this.options.height = con.offsetHeight || 30
            canvas.id = this.options.id + 'Canvas'
            canvas.width = this.options.width
            canvas.height = this.options.height
            canvas.style.cursor = 'pointer'
            con.appendChild(canvas)
            canvas.onclick = () => {
                this.refresh()
            }
        },

        /*刷新(生成)验证码*/
        refresh: function () {
            this.options.code = ''
            let canvas = document.getElementById(this.options.id + 'Canvas')
            let ctx = canvas.getContext('2d')
            ctx.clearRect(0, 0, canvas.width, canvas.height)
            ctx.textBaseline = 'middle'
            //设置背景颜色
            ctx.fillStyle = this.options.background || randomColor(180, 240)
            ctx.fillRect(0, 0, this.options.width, this.options.height)
            //判断验证码类型
            let txtArr = this.options.numArr.concat(this.options.letterArr)
            if (this.options.type == 'number') {
                txtArr = this.options.numArr
            } else if (this.options.type == 'letter') {
                txtArr = this.options.letterArr
            }
            for (let i = 1; i <= this.options.num; i++) {
                let txt = txtArr[randomNum(0, txtArr.length)]
                this.options.code += txt
                ctx.font = randomNum(this.options.height * .5, this.options.height * .8) + `px "${this.options.fontFamily}"` //随机生成字体大小
                ctx.fillStyle = randomColor(...this.options.colorArr) //随机生成字体颜色
                ctx.shadowOffsetX = randomNum(-3, 3)
                ctx.shadowOffsetY = randomNum(-3, 3)
                ctx.shadowBlur = randomNum(-3, 3)
                ctx.shadowColor = 'rgba(0, 0, 0, 0.3)'
                let x = this.options.width / (this.options.num + 2) * i
                let y = this.options.height / 2
                let deg = randomNum(-30, 30)//
                /*设置旋转角度和坐标原点*/
                ctx.translate(x, y)
                ctx.rotate(deg * Math.PI / 180)
                ctx.fillText(txt, 0, 0)
                /*恢复旋转角度和坐标原点*/
                ctx.rotate(-deg * Math.PI / 180)
                ctx.translate(-x, -y)
            }
            /*绘制干扰线*/
            if (this.options.hasLine) {
                for (let i = 0; i < 6; i++) {
                    ctx.strokeStyle = randomColor(40, 180)
                    ctx.beginPath()
                    ctx.moveTo(randomNum(0, this.options.width), randomNum(0, this.options.height))
                    ctx.lineTo(randomNum(0, this.options.width), randomNum(0, this.options.height))
                    ctx.stroke()
                }
            }

            /*绘制干扰点*/
            if (this.options.hasDot) {
                for (let i = 0; i < this.options.width / 4; i++) {
                    ctx.fillStyle = randomColor(0, 255)
                    ctx.beginPath()
                    ctx.arc(randomNum(0, this.options.width), randomNum(0, this.options.height), 1, 0, 2 * Math.PI)
                    ctx.fill()
                }
            }
        },

        /*验证验证码*/
        validate: function (ucode) {
            ucode = this.options.isStrict ? ucode : ucode.toLowerCase()
            let code = this.options.isStrict ? this.options.code : this.options.code.toLowerCase()
            if (ucode == code) {
                return true
            } else {
                this.refresh()
                return false
            }
        }
    }

    /*生成一个随机数*/
    function randomNum(min, max) {
        return Math.floor(Math.random() * (max - min) + min)
    }

    /*生成一个随机颜色*/
    function randomColor(min, max) {
        return `rgb(${randomNum(min, max)}, ${randomNum(min, max)}, ${randomNum(min, max)})`
    }

    window.zVerify = zVerify
})(window, document)