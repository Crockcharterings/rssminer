(ns rssminer.i18n)

(def messages {:m-logo-title ["tip: show subscription list when you hover the logo" "提示：hover显示订阅列表"]
               :m-signup-warn ["signup free, and save your subscriptions, get personal recommendation" "点击免费注册，注册后可添加自己的订阅，并得到个性化推荐"]
               :m-demo-account ["Create your own account, free" "免费创建您自己的帐户"]
               :m-search-placeholder ["search feed, subscription..." "搜索订阅或文章，快速直达..."]
               :m-change-avata ["Change your avatar at gravatar.com" "点击到gravatar.com更改头像"]
               ;; app.tpl menu
               :m-add-sub ["Add subscription" "添加订阅"]
               :m-back-home ["Back home" "回到首页"]
               :m-keyboard-shortcut ["Keyboard shortcuts" "键盘快捷键"]
               :m-settings ["Settings" "设置"]
               :m-search ["Search" "搜索"]
               :m-feedback ["Feedback" "提交反馈"]
               :m-logout ["Logout" "退出"]

               :m-submit ["Submit" "提交"]
               :m-placeholder-feedback ["Tell us your feedback about Rssminer. We together make a better RSS reader"
                                        "告诉我们你对Rssminer的意见或者建议吧，我们会认真听取，提供更好的RSS阅读"]
               :m-placeholder-email ["Email, in case we keep in touch. Optional."
                                     "邮箱，选填。以便我们与您进一步沟通"]

               :m-description ["Rssminer is yet another free RSS reader. Build by Feng Shen, with love of RSS subscription and reading."
                               "Rssminer，免费的RSS阅读器。专注于订阅读器，辅助于个性化推荐，致力于愉快，高效的阅读；为手机屏幕特别优化，随时随地最新文章；认真，只为使您用得舒心。"]
               :m-keywords ["RSS miner, Rssminer, RSS aggregator, simple, fast, intelligent, free"
                            "RSS miner, Rssminer, RSS aggregator, 简单，快速，免费"]

               ;; app.tpl
               :m-k-next ["Next item" "上一篇文章或者上一个订阅"]
               :m-k-prev ["Previous item" "下一篇文章或者下一个订阅"]
               :m-k-open ["Open first item / Open current in new tab" "在新窗口打开"]
               :m-k-scroll-down ["Scroll down article" "向下滚屏"]
               :m-k-scroll-up ["Scroll up article" "向上滚屏"]
               :m-k-return-list ["Return to list" "回到列表"]
               :m-k-focus-tab ["Focus next tab" "选择另外一个标签"]
               :m-k-focus-search ["Focus search" "焦点移到搜索框"]
               :m-k-show-help ["Bring up this help dialog" "打开这个帮助文档"]
               :m-k-go-home ["Go home" "回到首页"]
               :m-close ["Close" "关闭"]
               :m-k-close-cancel ["Close or cancel" "取消或者关闭"]

               ;; mobile landing page
               :m-login-with-google ["Login with Google OpenID" "以Google帐户登陆"]
               :m-yet-another ["Yet another free RSS reader" "认真，免费的RSS阅读器"]
               :m-login-error ["Login failed, Email or password error" "用户名或者密码错误，请检查"]
               :m-has-password ["Has an password enabled account?" "已有帐户？并设置过登陆密码"]
               :m-email ["Email:" "邮箱："]
               :m-password ["Password:" "登陆密码："]
               :m-login ["Login" "登陆"]
               :m-tryout ["Try out Rssminer", "随便看看"]
               :m-persistent ["Remember me" "记住我"]

               :m-sub-list ["Subscription list", "订阅列表"]

               ;; landing
               :m-site-title ["Rssminer, yet another free RSS reader"
                              "Rssminer, 认真，免费的RSS阅读器"]})

(def zh-messages (into {} (map (fn [[k [en zh]]] [k zh]) messages)))

(def en-messages (into {} (map (fn [[k [en zh]]] [k en]) messages)))

(def ^{:dynamic true} *req*)
