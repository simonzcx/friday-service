import urllib.request
from urllib.request import urlretrieve
import urllib.parse
from bs4 import BeautifulSoup
import re
from selenium import webdriver
import time
import requests
import os
import re
import sys


file_name = '任务'


def findMusicUrl(url):
    # 模拟用户点击事件
    driver = webdriver.Chrome()
    driver.get(url=url)
    driver.maximize_window()
    main_window = driver.current_window_handle
    _dict = []
    music_url = []
    # 请求头
    headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                             'Chrome/94.0.4606.71 Safari/537.36'}
    # 请求对象，默认是get方法，key的值会暴露在地址栏上，而post方法会封装好
    request = urllib.request.Request(url=url, headers=headers)
    try:
        # 发送请求获取响应
        response = urllib.request.urlopen(request, timeout=1.0)
        # 获取响应内容
        content = response.read().decode('utf-8')
        soup = BeautifulSoup(content, 'lxml')
        # print(soup.find_all(name='a'))
        print("爬取音乐播放地址:")
        n = 0
        for tag in soup.find_all(name='a'):
            if tag.get("href") is not None:
                href = tag.get("href")
                if str(href).startswith('/play/') and str(href).endswith('.htm'):
                    # name = BeautifulSoup(str(tag), 'html.parser').find('font')
                    # print(tag)
                    name = BeautifulSoup(str(tag), 'html.parser').text
                    if name is not None:
                        n = n + 1
                        _name = name
                        _href = 'https://www.9ku.com' + str(href)
                        # _dict保存的是播放该歌曲的网页链接，不是下载地址.mp3
                        _dict.append({'href': _href, 'name': _name})
                        # print(_name)
                        # link = driver.find_element_by_link_text(_name)
                        link = driver.find_element_by_xpath('//a[@href="' + str(href) + '"]')
                        link.click()
                        # time.sleep(5)
                        all_handles = driver.window_handles
                        driver.switch_to.window(all_handles[1])
                        text = driver.page_source
                        _text = BeautifulSoup(text, 'lxml')
                        # print(text)
                        for _tag in _text.find_all(name='audio'):
                            if str(_tag.get("src")).endswith('.mp3'):
                                print(str(n)+'、'+str(_tag.get("src")))
                                music_url.append({'url': str(_tag.get("src")), 'name': _name})
                                break
                        driver.close()
                        # 切换到主窗口
                        driver.switch_to.window(main_window)
        return _dict, music_url
    except urllib.error.URLError as ex:
        print('请求错误')


def downloadMusic(singer_name, music_dict):
    global file_name
    folder_path = "./music_get_by_url/music_download/{}".format(singer_name)
    if not os.path.exists(folder_path):
        print("The selected folder does not exist, try to create it.")
        os.makedirs(folder_path)
    for m in music_dict:
        try:
            file_name = m['name']
            urlretrieve(m['url'], folder_path + '/' + clean_file_name(m['name']) + '.mp3', schedule)
        except urllib.error.HTTPError as e:
            print('Download failed:' + e)


def clean_file_name(filename: str):
    # []:表示字符集，一个字符的集合，可匹配其中任意一个字符
    # \\:转义字符，跟在其后的字符将失去作为特殊元字符的含义，例如\\.只能匹配.，不能再匹配任意字符
    invalid_chars = '[\\\/:*?"<>|]'
    replace_char = '-'
    return re.sub(invalid_chars, replace_char, filename)


def schedule(block_num, block_size, total_size):
    """
    blocknum:当前已经下载的块
    blocksize:每次传输的块大小
    totalsize:网页文件总大小
    """
    if total_size == 0:
        percent = 0
    else:
        percent = block_num * block_size / total_size
    if percent > 1.0:
        sys.stdout.write('\r>> Downloading %s %.1f%%\n' % (file_name, 100))
        time.sleep(0.0005)
        sys.stdout.flush()
    else:
        percent = percent * 100
        sys.stdout.write('\r>> Downloading %s %.1f%%' % (file_name, percent))
        time.sleep(0.0005)
        sys.stdout.flush()


# 通过歌曲播放链接爬取失败，因为页面逻辑是跳转的
def findLoadUrl(music_dict):
    for m in music_dict:
        # 1、方法一
        # chromedriver的绝对路径
        driver_path = r'C:\Program Files (x86)\Google\Chrome\Application\chromedriver.exe'
        # 初始化一个driver，并且指定chromedriver的路径
        driver = webdriver.Chrome(executable_path=driver_path)
        # 请求网页
        driver.get(m['href'])
        time.sleep(2)
        driver.quit()
