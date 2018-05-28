# 谷米财富接口文档

# 1. 概述

## 1.1 关于谷米财富

谷米财富是一家综合理财超市，通过严格的标准，从数千家理财平台中甄选出优质平台，为用户提供高收益，分散投资，以及一站式跨平台投资的便捷服务，是国内领先的互联网金融第三方信息服务商。

## 1.2 设计背景

谷米财富与优质的P2P平台合作，通过技术手段对接，为给用户提供良好的一站式登录、一站式投资体验，既为用户提供了安全、方便、快捷的网贷投资服务，也为合作平台带来了更多的用户。

## 1.3 文档范围

此文档面向接口开发者介绍谷米财富的接口协议、如何实现、相应示例，以及相关注意事项。

## 1.4 阅读对象

谷米财富平台、P2P网贷平台的商务经理、产品经理、开发人员、测试人员、运维人员。

# 2. 总体说明

## 2.1 接口调用

谷米财富通过`HTTP POST`的方式调用平台实现的接口

平台提供单一入口的URL(除了bindUser和login接口),用来接收调用请求

### 调用方式

```shell
curl -X POST -d '<data>' '(http|https)://<url>'
```

## 2.2 数据格式

无论是请求还是响应，数据格式都是JSON，且数据都须满足一定的Schema（模式）,Schema定义如下

```json
{
  "data": "string, required",
  "timestamp": "int64, required",
  "nonce": "string, required",
  "signature": "string, required"
}
```

### data原始数据模式定义

```json
{
	"service": "string, required, 用于区分不同的接口调用",
	"body": "object, optional, 请求数据，JSON格式，具体参考接口定义章节"
}
```

### Schema各字段解释

字段名 | 类型 | 必填 | 描述
--- | --- | --- | ---
data | string | Yes | 请求或响应数据，该字段为加密后的数据（加密算法请参考数据安全性章节），加密前的原始数据是`JSON`格式（参考data原始数据模式定义）
timestamp | int64 | Yes | 时间戳，数据发送的当前时间，在数据加密和校验时有重要作用
nonce | string | Yes | 随机字符串，用于data的签名，如果data为空，该字段也为空
signature | string | Yes | 对data的签名，如果data为空，该字段也为空，具体签名算法见`2.3.4`章节

### 错误响应

如果程序逻辑出错，需要返回错误的响应，错误响应的`HTTP Status Code`定义为`500`，错误的响应内容要有统一的格式，定义如下

```json
{
  "code": "int, 错误代码，具体见接口定义章节",
  "message": "错误描述"
}
```
注意：错误响应内容无需加密

### 其他说明

- 字符编码使用`UTF-8`
- 协议中所有时间参数（如`registerAt`, `investAt`等等）格式均为`2006-01-02 15:04:05`
- 如果某字段无值请填写null(如`status`:null)

## 2.3 数据安全

为保证数据安全性，对每个接口的调用（无论是请求还是响应）都需要加密，下面具体的描述了加密的细则

### 2.3.1 时间戳

这里指2.2章节中提到的协议中的时间戳。

程序逻辑在处理接收到的请求或响应时，需要根据该时间戳校验协议的时效性，如果

```
当前时间戳 - 协议时间戳 > 5分钟
```

则需要拒绝该请求或者响应。


### 2.3.2 密钥

谷米财富会为每个平台分配一个密钥（Secret），为了保证密钥的安全性，在每次请求时都会生成一个请求密钥，使用请求密钥对数据进行加密。

```php
// 请求密钥的生成方法为
ReqKey = MD5(Secret + Timestamp)
```

变量名 | 描述
--- | ---
ReqKey | 请求密钥，用于加密请求或响应数据
Secret | 分配密钥，谷米财富为每个平台分配
Timestamp | 时间戳，单位秒，数据发送的当前时间

### 2.3.3 加解密

默认使用`AES`方式对数据进行加密，数据加密前会对数据做一些简单的处理。

使用`Data`表示真实的明文数据

```php
// 加密算法
RawData = RandomStr + DataLength + Data + PlatId;
EncryptData = AESEncrypt(RawData, ReqKey);
```

变量名 | 描述
--- | ---
RandomStr | 16个字节的随机字符串
DatadLength | 数据长度，固定4个字节
Data | 明文数据
PlatId | 谷米财富分配给平台的ID
RawData | 加密前的数据
EncryptData | 加密后的数据
ReqKey | 请求密钥

### 2.3.4 数据签名

为了防止数据被篡改，对加密后的数据进行签名，接收到数据后会需要校验签名。

```php
// 数据签名算法
Signature = SHA1(sort(EncryptData, Token, Timestamp, Nonce));
```

变量名 | 描述
--- | ---
EncryptData | 加密后的数据
Token | 谷米财富分配给平台的Token
Timestamp | 时间戳，对应2.2中的timestamp
Nonce | 随机字符串，对应2.2中的nonce
Signature | 签名

# 3. 接口描述

## 3.1 创建新账户

谷米财富通过该接口，为谷米财富用户在合作平台创建一个新的帐号。

### Service

`service=createUser`

### Request

```json
{
  "username": "string, 谷米财富用户名(可选)",
  "platformUserNo":"string 谷米财富平台用户编号",
  "telephone":"string, 手机",
  "email": "string, 电子邮箱(可选)",
  "idCard": {
    "number": "string, 身份证号码",
    "name": "string, 实名"
  }, 
  "bankCard": {
	  "number": "string, 卡号",
	  "bank": "string, 银行名称",
	  "branch": "string, 支行名",
	  "province": "string, 省份",
	  "city": "string, 城市",
  },
  "tags": "array, 标签 (wap,pc)"
}
```

### Response

```json
{
  "username": "string, required, 谷米财富用户名(可选)",
  "platformUserNo":"string 谷米财富平台用户编号",
  "usernamep":"string, required, 平台用户名",
  "registerAt": "datetime, required, 平台注册时间",
  "bindAt": "datetime, required, 绑定谷米财富时间",
  "bindType": "enum, required, 0:表示谷米财富带来的新用户",
  "salt": "string, 用于鉴权校验,该账户的8-16位长度密钥",
  "tags": "array, 标签"
}
```

- 如果谷米财富平台用户编号重复创建，同样视为成功，返回对应的绑定信息。
- 绑定谷米财富时间：是指通过谷米过来注册的用户，在第三方平台进行绑定的时间。

### Errors

code | message
--- | ---
1001 | 手机号已占用
1002 | 邮箱已占用
1003 | 身份证已占用
1004 | 平台用户编号已占用


## 3.2 关联老账户

谷米财富通过该接口，将用户在谷米财富的帐号跟在合作平台的帐号关联起来。

### Service

`service=bindUser`

### Request

```json
{
  "username": "string, 谷米财富用户名(可选)",
  "platformUserNo":"string 谷米财富平台用户编号",
  "telephone": "string, 手机",
  "email": "string, 电子邮箱(可选)",
  "idCard": {
    "number": "string, 身份证号码",
    "name": "string, 实名"
  },
  "bankCard": {
	  "number": "string, 卡号",
	  "bank": "string, 银行名称",
	  "branch": "string, 支行名",
	  "province": "string, 省份",
	  "city": "string, 城市",
  },
  "tags": "array, 标签 (wap,pc)"
}
```

### Response

合作平台接收到该请求后，需要将用户带到谷米财富与合作平台的专属绑定页面，验证用户身份。验证成功后，完成绑定。
用户授权绑定成功后平台需同步回调谷米财富的接口URL.

	线上地址：https://www.gumilicai.com/open/callback
	测试地址：http://test.gumilicai.com/open/callback

此时请求的URL 所需参数为：
### Service
`service=bindUser`
### Request
```json
{
  "username": "string, required, 谷米财富用户名(可选)",
   "platformUserNo":"string 谷米财富平台用户编号",
  "usernamep": "string, required, 平台用户名",
  "registerAt": "datetime, required, 平台注册时间",
  "bindAt": "datetime, required, 绑定谷米财富时间",
  "bindType": "enum, required, 1:表示平台已有用户",
  "salt": "string, 用于鉴权校验,该账户的8位长度密钥",
  "tags": "array, 标签"
}
```

请求回调的Method 为 `POST` 参数为
`data=谷米财富&nonce=xxx&signature=xxxx&timestamp=12345643&appId=xxxx`


### Response

谷米财富会输出提示用户绑定成功的页面


## 3.3 单点登录

谷米财富与合作平台帐户关联的用户，通过该接口登录到合作平台。

### Service

`service=login`

### Request

```json
{
  "username": "string, 谷米财富用户名(可选)",
  "usernamep": "string, 合作平台用户名",
  "salt": "string, 用于鉴权校验,该账户的8位长度密钥",
 "bid": "string, 标的ID，跳转到标的购买页，home为首页，account为个人中心,recharge为充值,withdrawals为提现",
  "type": "登录类型，0:PC，1:WAP"
}
```


请求回调的Method 为 `POST` 参数为
`data=谷米财富&nonce=xxxx&signature=xxxx&timestamp=12345643`

### Response

该接口为非应答接口，而是平台设置该用户的登录态，并进行浏览器跳转


## 3.4 用户信息查询

谷米财富通过该接口获取到合作平台的绑定用户信息

### Service

`service=queryUser`

### Request

```json
{
	"timeRange": {
		"startTime": "开始时间",
		"endTime": "结束时间"
	},
	"index": {
		"name": "这里只会根据谷米财富平台用户编号查询，固定为platformUserNo",
		"salt": "string, 用于鉴权校验,该账户的8位长度密钥",
		"vals": "platformUserNo数组，查询匹配的用户信息"
	}
}
```

可以根据时间的范围查询（timeRange）这个时间范围内完成绑定的所有用户信息，也可以根据索引查询（index）指定用户名的用户信息

### Response
```json
[
	{
	  "username": "string, required, 谷米财富用户名(可选)",
	   "platformUserNo":"string 谷米财富平台用户编号",
	  "usernamep": "string, required, 平台用户名",
	  "registerAt": "datetime, required, 平台注册时间",
	  "bindAt": "datetime, required, 绑定谷米财富时间",
	  "bindType": "enum, 0:表示谷米财富带来的新用户，1:表示平台已有用户",
	  "assets": {
		 "awaitAmountCapital": "float, 待收本金",
		 "awaitAmountInterest": "float, 待收利息",
		 "currentAmount": "float, 活期金额",
		 "frozenAmount": "float, 冻结金额",
		 "balanceAmount": "float, optional, 账户余额",
		 "totalAmount": "float, optional, 资产总额"
	  },
	  "coupons": [
		 {
		 "id": "string, 券唯一标识",
		 "name": "string, 券名",
		 "amount": "float, 券的金额",
		 "rate": "float, 券的利率",
		 "type": "enum, 券的类型",
		 "desc": "string, 券的详情",
		 "status": "enum, 券的状态 0:可使用, 1:已过期, 2:已使用"
		 },
	  ],
	  "tags": "array, 标签"
	}
]
```
#### 券的类型

type | 说明
--- | ---
0 | 现金券
1 | 加息券
2 | 红包
9 | 其他

## 3.5 标的信息查询

谷米财富通过该接口获取到合作平台的标的信息，用于

- 谷米财富向用户展示合作平台标的
- 更新标的状态

### Service

`service=queryBids`

### Request

```json
{
	"timeRange": {
		"startTime": "开始时间",
		"endTime": "结束时间"
	},
	"index": {
		"name": "这里只会根据标的ID查询，固定为id",
		"vals": "array，标ID数组"
	}
}
```

可以根据时间的范围查询（timeRange）这个时间范围内**创建的所有标**的信息，也可以根据索引查询（index）指定标的ID的标的信息


### Response

```json
[
	{
	  "id": "string, 标的ID",
	  "url": "string, 标的URL",
	  "title": "string, 标题",
	  "desc": "string, 描述",
	  "borrower": "string, 借款人",
	  "borrowAmount": "float, 借款金额",
	  "remainAmount": "float, 剩余金额",
	  "minInvestAmount": "float, 起投金额",
	  "period": "string, 借款期限类型, 1 天 2月，3年 如果为活期该字段为0",
	  "deadline":"string, 借款期限",
	  "originalRate": "float, 原始年化利率，13.5表示13.5%",
	  "rewardRate": "float, 奖励利率，13.5表示13.5%",
	  "status": "enum, 标的状态，见标的状态表格",
	  "repayment": "string, 还款方式",
	  "type": "enum, 见标的类型表格",
	  "prop": "string，标的性质,比如:车贷，房贷，信用贷、融资租赁、保理等等",
	  "createAt": "datetime, 标的创建时间",
	  "publishAt": "datetime, 标的起投时间，如果有倒计时，这个时间会晚于标的创建时间",
	  "closeAt": "datetime, 标的截止购买时间",
	  "fullAt": "datetime, 标的满标时间",
	  "repayDate": "date, 预计还款日期(最后一期)",
	  "device": "enum, 0:pc/wap展示, 1:pc展示, 2:wap展示(非必传项)",
	  "tags": "标签，数组，用以扩充标的属性。如：标的活动信息"
	}
]
```

#### 标的类型

type | 说明
--- | ---
1 | 普通标
2 | 转让标
3 | 净值标
4 | 新手标
5 | 体验金标
6 | 活期
7 | 其他（如：秒还标）

#### 标的状态

status | 说明
--- | ---
0 | 还款中
1 | 已还清
2 | 逾期
3 | 投标中
4 | 流标
5 | 撤标
6 | 满标
7 | 放款
8 | 等待放款
9 | 等待开始
99 | 其他

#### 期限类型

period | 说明
--- | ---
0 | 活期
1 | 天标
2 | 月标


## 3.6 投资记录查询

谷米财富通过该接口获取到谷米财富用户在合作平台的投资记录，展示在谷米财富的个人中心，用户可以通过投资记录，了解在各个平台的投资情况，并通过链接再次进入到合作平台，从而为合作平台导流。

### Service

`service=queryInvests`

### Request

```json
{
	"timeRange": {
		"startTime": "开始时间",
		"endTime": "结束时间"
	},
	"index": {
		"name": "id OR bid OR platformUserNo",
		"salt": "string, 用于鉴权校验,该账户的8位长度密钥(以platformUserNo查询时才有该字段)",
		"vals": "array，见下面的说明"
	}
}
```

可以根据时间的范围查询（timeRange）这个时间范围内发生的所有投资记录，也可以根据索引查询，进行如下查询

- id查询（name="id"），查询指定投资ID的投资记录，一个id对应一条投资记录
- bid查询（name="bid"），查询指定标的ID的投资记录列表，一个bid对于多条投资记录
- platformUserNo（name="platformUserNo"），查询指定用户的一段时间范围内的投资记录，**必须有timeRange参数**

### Response

```json
[
	{
	  "id": "string, 投资记录ID，全局唯一",
	  "bid": "string, 标的ID",
	  "burl": "string, 标的url",
	  "username": "string, 合作平台用户名",
	  "platformUserNo":"string 谷米财富平台用户编号",
	  "amount": "float, 投资金额",
	  "actualAmount": "float, 实际投资金额",
	  "income": "float, 预期投资收益",
	  "investAt": "datetime, 投资时间",
	  "status": "enum, 标的状态，见标的状态表格(非必传项)",
	  "trans_state": "enum, 0未转让 1表示转让",
	  "trans_time": "datetime 转让时间 默认为空，已转让：格式2014-09-01 19:30:12精确到秒",
	  "end_time": "datetime 交易结束时间(投资到期时间)格式2014-09-01",
	  "all_back_principal": "float 已回款本金",
	  "all_back_interest": "float 已回款利息",
	  "tags": "array, 标签"
	}
]
```

tips: 从移动端投资用`wap`标识，Andoird客户端用`android` iOS客户端用`ios`标识，其他用`pc`标识，自动购买用`auto_buy`标识

# 4. 异常

下表为每个接口公用的一些异常

type| name | 说明 
--- | ---- | ---  
101 | MISSING_SERVICE_NAME     | 缺少 Service Name 
102 | UNKNOWN_SERVICE_ERROR    | Service Name不存在
103 | VALIDATE_SIGNATURE_ERROR | 签名验证失败
104 | VALIDATE_TIMESTAMP_ERROR | 时间戳过期
105 | VALIDATE_APPID_ERROR 	   | AppID校验失败
106 | PARSE_JSON_ERROR 		   | JSON反序列化出错
107 | GEN_RETURN_MSG_ERROR	   | 生成返回包失败
108 | COMPUTE_SIGNATURE_ERROR  | 生成签名失败
109 | ENCRYPT_AES_ERROR		   | 加密失败
110 | DECRYPT_AES_ERROR		   | 解密失败
201 | INVALID_PARAMETER		   | 请求参数出错
202 | USER_NOT_EXISTS		   | 用户不存在
203 | START_GREAT_THAN_END	   | startTime 不能大于endTime
204 | TIME_RANGE_EXCEED		   | 时间查询跨度不能超过72小时
205 | QUERY_ITEM_COUNT_EXCEED  | 查询项数量过多
500 | APPLICATION_ERROR        | 系统异常
1001 | TELEPHONE_HAVE_USED     | 手机号已占用
1002 | EMAIL_HAVE_USED 		   | 邮箱号已占用
1003 | IDCARD_HAVE_USED 	   | 身份证号已占用
1004 | USERNAME_HAVE_USED 	 | 平台用户编号已占用
1005 | JSON_MAPPING_EXCEPTION 	 | JSON映射异常
