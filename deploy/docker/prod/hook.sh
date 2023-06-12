#!/bin/bash

# 指定证书和密钥的源目录
source_certs_dir=/data/certs/certs
source_keys_dir=/data/certs/private

# 指定目标目录
target_dir=/data/certs/

# 获取所有的域名
domains=$(ls $source_certs_dir | sed -e 's/\(.*\)\..*/\1/')

# 遍历每个域名
for domain in $domains
do
  # 如果目标目录不存在，创建它
  if [ ! -d "$target_dir/$domain" ]; then
    mkdir -p "$target_dir/$domain"
  fi

  # 移动 .crt 和 .key 文件到目标目录
  cp "$source_certs_dir/$domain.crt" "$target_dir/$domain/$domain.crt"
  cp "$source_keys_dir/$domain.key" "$target_dir/$domain/$domain.key"
done
