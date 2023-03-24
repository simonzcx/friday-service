## 简介
云服务上的普通用户可以免密码使用sudo，自搭建的系统却不行。
以ubuntu为例修改`/etc/sudoers`文件实现，其他Linux类似。

## 步骤
### 1. 切换root用户
> sudo -i

### 2. 给sudoers文件添加写入权限
> chmod u+w /etc/sudoers

### 3. 编辑sudoers文件
> vim /etc/sudoers
```bash
# User privilege specification
root	ALL=(ALL:ALL) ALL
# Members of the admin group may gain root privileges
%admin ALL=(ALL) ALL
# Allow members of group sudo to execute any command
%sudo	ALL=(ALL:ALL) ALL
# 添加一行，username是你的用户名 添加这一行就能免密sudo了
username ALL=(ALL:ALL) NOPASSWD: ALL
```

### 4. 关闭sudoers文件写入权限
> chmod u-w /etc/sudoers
