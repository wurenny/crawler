(function() {
    QHPass.resConfig.src = "pcw_so",
    QHPass.resConfig.cookie_domains = ["so|360"],
    QHPass.resConfig.postCharset = "utf-8",
    QHPass.resConfig.loginOpts.loginType = "quick",
    QHPass.resConfig.loginOpts.thirdLogin = ["sina|renren|msn|fetion|Telecom", "pcw_so", "pop", "http://www.so.com"],
    QHPass.resConfig.thirdFun = function() {
        location.reload(!0)
    },
    $("#user-login").bind("click",
    function() {
        return QHPass.login(function() {
            window.location.reload()
        }),
        !1
    }),
    $("#user-reg").bind("click",
    function() {
        return QHPass.reg(function() {
            window.location.reload()
        }),
        !1
    }),
    $("#user-logout").bind("click",
    function() {
        return QHPass.logout(),
        !1
    }),
    $(document.body).delegate(".pop-dia-close", "click",
    function() {
        return window.location.reload(),
        !1
    }),
    QHPass.getUserInfo(function(e) {
        if (typeof e != "undefined") {
            var t = '<span class="hd-sep">|</span><a href="http://i.360.cn/?src=pcw_so">' + e.username + '</a>&nbsp;&nbsp;<a href="http://login.360.cn/?src=pcw_so&op=logout&destUrl=http%3A%2F%2Fwww.so.com%2F" target="_self">\u9000\u51fa</a>';
            $("#hd-user").html(t),
            monitor.setConf("clickUrl", "http://s.360.cn/sou/login.gif").log({
                src: So.comm.src,
                ref: document.referrer,
                qid: e.qid,
                abv: So.web.params.abv
            },
            "click")
        }
    })
})(),
function(e, t, n, r, i) {
    function s(i) {
        e._iwtLoading = 1,
        n = t.createElement("script"),
        n.src = r.URL,
        i = t.getElementsByTagName("script"),
        i = i[i.length - 1],
        i.parentNode.insertBefore(n, i)
    }
    r = {
        UA: "UA-360-000003",
        NO_FLS: 1,
        WITH_REF: 0,
        URL: "/resource/js/iwt-min.js"
    },
    e._iwt ? e._iwt.track(r, i) : (e._iwtTQ = e._iwtTQ || []).push([r, i]),
    !e._iwtLoading && s()
} (this, document),
function() {
    window.HUID = {
        key: "__huid",
        set: function(e) {
            var t = this,
            n = new Date;
            n.setMinutes(n.getMinutes() + 60),
            Tool.cookie.set(t.key, e.huid, n, "/", ".so.com")
        },
        pull: function() {
            $.getScript("http://huid.ad.360.cn/?api=set&cb=HUID.set")
        },
        init: function() {
            var e = this;
            Tool.cookie.get(e.key) == null && e.pull()
        }
    },
    window.HUID.init()
} ()