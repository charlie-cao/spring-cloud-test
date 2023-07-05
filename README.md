# spring-cloud-test




1 使用github 作为仓库: 开发新功能使用 分支，合并时触发 github action

2 增加make文件 docker 文件 docker compose 用于本地挂载开发

3 使用github action进行打包

4 推送到docker hub仓库

5 在github action中连接到远程k8集群执行部署 部署使用yaml

------------
完整的云原生开发

只要连接上github，就可以进行开发，包括环境搭建以及应用开发测试和部署。全面使用云技术，告别客户端。和主机。

用一个最简单的例子来展现整个过程

第一步创建预览环境和生产环境

预览环境在代码提交时就会进行展示，前端，以及后端微服务都可以。
------------


首先，您需要按照以下步骤执行：

安装 Terraform：

前往 Terraform 官方网站（https://www.terraform.io）下载适合您操作系统的安装包。
安装 Terraform，并将其可执行文件添加到系统路径。
创建 Terraform 配置文件：

创建一个新的目录，用于存放 Terraform 配置文件。
在该目录中创建一个新的文本文件，命名为 main.tf，用于定义 Terraform 的主要配置。
编写 Hetzner Cloud 提供商配置：

在 main.tf 文件中，配置 Hetzner Cloud 的提供商，包括 API 令牌和其他选项。
定义虚拟机资源：

在 main.tf 文件中，使用 Hetzner Cloud 的 Terraform 提供商定义虚拟机资源。
您可以指定虚拟机的大小、数量、镜像、网络设置等。
创建 Ansible Playbook：

在同一目录中创建一个新的 YAML 文件，命名为 playbook.yml（或其他你喜欢的名称），用于编写 Ansible Playbook。
在 Playbook 中定义一系列任务和配置，以安装和配置 Kubernetes 多节点集群。
编写 Ansible Playbook 任务：

在 Playbook 文件中使用 YAML 格式编写任务，包括安装 Docker、配置网络、安装 Kubernetes 组件等。
在 Terraform 配置中调用 Ansible Playbook：

在 main.tf 文件中，使用 null_resource 和 local-exec 来调用 Ansible Playbook。
指定 local-exec 命令以运行 Ansible Playbook，并传递必要的参数和变量。
执行 Terraform：

在命令行中导航到包含 Terraform 配置文件的目录。
运行 terraform init 命令，初始化 Terraform，下载所需的提供商插件和模块。
运行 terraform plan 命令，检查配置文件并生成执行计划。
运行 terraform apply 命令，应用配置并创建虚拟机。
通过将 Terraform 和 Ansible 结合使用，您可以实现自动化地在 Hetzner Cloud 上创建虚拟机，并使用 Ansible 安装和配置 Kubernetes 多节点集群。请注意，此过程可能需要您具备一定的 Terraform 和 Ansible 的知识和经验，以适应您的特定环境和需求。建议参考 Terraform 和 Ansible 的官方文档以获取更详细的信息和指南。


