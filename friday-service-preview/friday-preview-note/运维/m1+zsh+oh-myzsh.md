## M1 Mac配置 zsh+oh-my-zsh+插件
### 1 Mac内置Shell
Mac 系统内置了几种 shell<br />
```
cat /etc/shells
```

### 2 更换Shell
- m1 芯片的全新 Mac 电脑使用 `/bin/zsh` 作为默认的 shell，因此不需要再安装 zsh
- 可以在终端下使用 `echo $SHELL` 命令来查看当前的 shell
- 若要更换 shell，使用`chsh -s /bin/bash`命令

### 3 安装 oh-my-zsh
自动安装
```
sh -c "$(curl -fsSL https://raw.github.com/ohmyzsh/ohmyzsh/master/tools/install.sh)"
```

### 4 安装 oh-my-zsh 插件
目前需要安装的插件有：
- zsh-autosuggestions     历史命令自动提醒
- zsh-syntax-highlighting 命令高亮
```
cd ~/.oh-my-zsh/custom/plugins/
git clone git@github.com:zsh-users/zsh-autosuggestions.git
git clone git@github.com:zsh-users/zsh-syntax-highlighting.git
```
修改 ~/.zshrc 文件，在插件列表中填入插件名称，如下所示：
`vim ~/.zshrc`
```
# Which plugins would you like to load?
# Standard plugins can be found in $ZSH/plugins/
# Custom plugins may be added to $ZSH_CUSTOM/plugins/
# Example format: plugins=(rails git textmate ruby lighthouse)
# Add wisely, as too many plugins slow down shell startup.
plugins=(git zsh-autosuggestions zsh-syntax-highlighting)
```
重新加载 ~/.zshrc 文件
```
source ~/.zshrc
```
### 5 brew安装rectangle
```
git config --global --unset http.proxy
git config --global --unset https.proxy

brew install rectangle
```

### 6 SSH免密
```
ssh-copy-id -i ~/.ssh/id_rsa.pub root@192.168.1.111
```
