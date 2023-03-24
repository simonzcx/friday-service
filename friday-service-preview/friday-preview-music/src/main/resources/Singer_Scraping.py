import urllib.request
import urllib.parse
from bs4 import BeautifulSoup
import re
from pypinyin import pinyin
import Music_Scraping

baseUrl = 'https://www.9ku.com/geshou'
# 请求头
headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) '
                         'Chrome/94.0.4606.71 Safari/537.36'}
# 歌手信息
singer_dict = []


def findSinger(name):
    character = pinyin(name)[0][0][0].lower()
    try:
        # 请求对象，默认是get方法，key的值会暴露在地址栏上，而post方法会封装好
        request = urllib.request.Request(url=baseUrl + '/all-' + character + '-all.htm', headers=headers)
        # 发送请求获取响应
        response = urllib.request.urlopen(request)
        # 获取响应内容
        content = response.read().decode('utf-8')
        soup = BeautifulSoup(content, 'lxml')
        for tag in soup.find_all(name='a'):
            if tag.get("href") is not None:
                href = str(tag.get("href"))
                if href.startswith('/geshou/') and href.endswith('.htm'):
                    pattern = re.compile(r'\d+')  # 查找数字
                    num = pattern.findall(href)
                    if len(num) == 1:
                        if tag.text.replace('\n', '').strip() is not "":
                            singer_dict.append({'href': href, 'name': tag.text.replace('\n', '').strip()})
        # 由于往后需要用ajax异步加载，通过分析网页，我们可以发现接下来的人都放在 https://www.9ku.com/geshou/all-z-all/n.htm （n为整数）
    except urllib.error.URLError as ex:
        print('请求错误' + str(ex))

    # 开始搜索
    ajax_dict = singer_dict
    # 目标字典
    _dict = []
    n = 0
    ajax_content = content
    while ajax_content != '':
        print("第" + str(n + 1) + "轮查找结束")
        for i in range(len(singer_dict)):
            if ajax_dict[i]['name'].lower() == name.lower():
                _dict = ajax_dict[i]
                return _dict
        # 开始异步搜索
        _dict = []
        n = n + 1
        ajax_url = baseUrl + '/all-' + character + '-all/' + str(n) + '.htm'
        ajax_request = urllib.request.Request(url=ajax_url, headers=headers)
        try:
            # 发送请求获取响应
            ajax_response = urllib.request.urlopen(ajax_request)
            # 获取响应内容
            ajax_content = ajax_response.read().decode('utf-8')
            ajax_soup = BeautifulSoup(ajax_content, 'lxml')
            for ajax_tag in ajax_soup.find_all(name='a'):
                if ajax_tag.get("href") is not None:
                    ajax_href = str(ajax_tag.get("href"))
                    if ajax_href.startswith('/geshou/') and ajax_href.endswith('.htm'):
                        ajax_pattern = re.compile(r'\d+')  # 查找数字
                        ajax_num = ajax_pattern.findall(ajax_href)
                        if len(ajax_num) == 1:
                            if ajax_tag.text.replace('\n', '').strip() is not "":
                                if ajax_tag.text.replace('\n', '').strip().lower() == name.lower():
                                    _dict = {'href': ajax_href, 'name': ajax_tag.text.replace('\n', '').strip()}
                                    return _dict
        except urllib.error.URLError as ajax_ex:
            print('请求错误' + str(ajax_ex))
    return _dict


if __name__ == '__main__':
    singerName = str(input("请输入你要查找的歌手的名字:"))
    if singerName is not '':
        result = findSinger(singerName)
        if not result:
            print('抱歉,查无此人')
        else:
            url = 'https://www.9ku.com' + result['href']
            print("目标地址:"+url)
            musicUrlDict, music_list = Music_Scraping.findMusicUrl(url)
            Music_Scraping.downloadMusic(singerName, music_list)
