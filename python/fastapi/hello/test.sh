# 基本测试
http localhost:8000/hi
http -b localhost:8000/hi
http -v localhost:8000/hi

# 路径测试
http -v localhost:8000/hipath/wangwu

# 参数测试
http -v localhost:8000/hiparam?who=qiuliu

# BODY测试
http -v localhost:8000/hibody who=boy

# Header测试
http -v POST localhost:8000/hihead who:girl
http -v POST localhost:8000/agent

# 测试状态码
http localhost:8000/happy

# 测试接口
http localhost:8000/header/car/baojun
http -v -a me:secret localhost:8000/me

