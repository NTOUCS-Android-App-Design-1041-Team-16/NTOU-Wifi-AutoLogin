# Bash shell script

## Orinx 慷慨捐贈
```bash
#!/bin/sh
#Your NTOU Account

username=
password=

# Develop option

useragent='Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:26.0) Gecko/20100101 Firefox/26.0'
debug_mode='1>/dev/null'

# for ruckus network

ruckus_server='140.121.40.253'

curl -c login.cookie -A "$useragent" -k -i -d "username=$username&password=$password&ok=%E7%99%BB%E5%85%A5" "https://$ruckus_server/user/user_login_auth.jsp" 2>/dev/null $debug_mode

curl -b login.cookie -A "$useragent" -k -i "https://$ruckus_server/user/user_login_auth.jsp" 2>/dev/null $debug_mode

curl -b login.cookie -A "$useragent" -k -i "https://$ruckus_server/user/_allowuser.jsp" 2>/dev/null $debug_mode

rm login.cookie

# for arubanetworks

arubanetworks_domain='securelogin.arubanetworks.com'
curl -k -i -d "user=$username&password=$password&cmd=authenticate&Login=Log+In" "https://$arubanetworks_domain/cgi-bin/login" 2>/dev/null $debug_mode

```