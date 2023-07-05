以下是一个简单的 Terraform 配置文件示例，用于在 Hetzner Cloud 上创建虚拟机，并使用 Ansible 安装 Kubernetes 多节点集群：

hcl
Copy code
# 设置 Terraform 的提供商和版本
terraform {
  required_providers {
    hcloud = {
      source  = "hetznercloud/hcloud"
      version = "1.32.0"
    }
  }
}

# 配置 Hetzner Cloud 提供商凭证
provider "hcloud" {
  token = "YOUR_HETZNER_API_TOKEN"
}

# 定义虚拟机资源
resource "hcloud_server" "kubernetes_nodes" {
  count      = 3  # 设置所需的节点数量
  name       = "kubernetes-node-${count.index}"
  image      = "ubuntu-20.04"
  server_type = "cx21"  # 设置虚拟机规格
  ssh_keys   = ["YOUR_SSH_KEY_ID"]  # 添加用于 SSH 登录的公钥

  # 网络设置
  network {
    network_id = "YOUR_NETWORK_ID"
    ip         = "YOUR_IP_ADDRESS"
  }

  provisioner "local-exec" {
    command = "ansible-playbook -i '${self.ipv4_address},' playbook.yml"
    working_dir = "${path.module}"
    environment = {
      ANSIBLE_HOST_KEY_CHECKING = "False"
    }
  }
}
请注意，在上述代码中，您需要将以下值替换为实际的参数：

YOUR_HETZNER_API_TOKEN：您的 Hetzner Cloud API 令牌。
YOUR_SSH_KEY_ID：用于 SSH 登录的 Hetzner Cloud SSH 密钥 ID。
YOUR_NETWORK_ID：您要连接虚拟机的网络 ID。
YOUR_IP_ADDRESS：为每个虚拟机分配的 IP 地址。
此外，您还需要创建一个名为 playbook.yml 的 Ansible Playbook 文件，用于安装和配置 Kubernetes 多节点集群。在 hcloud_server 资源中的 provisioner 部分，我们使用 local-exec 执行 Ansible Playbook 命令，并传递所需的参数。

请确保在同一目录中存在 playbook.yml 文件，并包含适当的 Ansible 任务和配置。您可以根据您的需求自定义 Ansible Playbook。

在完成配置文件后，您可以按照以下步骤运行 Terraform：

打开终端或命令提示符，导航到包含 Terraform 配置文件的目录。

运行以下命令，初始化 Terraform：

csharp
Copy code
terraform init
运行以下命令，预览将要创建的资源：

Copy code
terraform plan
确认计划中将要创建的虚拟机实例和其他资源。

运行以下命令，应用配置并创建资源：

Copy code
terraform apply
根据提示确认执行计划，Terraform 将开始创建 Hetzner Cloud 上的虚拟机实例，并在每个实例上运行 Ansible Playbook 安装 Kubernetes 多节点集群。

请注意，上述代码和步骤仅提供了一个简单的示例，用于在 Hetzner Cloud 上创建虚拟机和安装 Kubernetes。在实际环境中，您可能需要根据您的需求进行更多的定制和配置。建议参考 Terraform 和 Ansible 的官方文档以获取更详细的信息和指南。





User
playbook.yml 的具体代码
ChatGPT
以下是一个简单的 Ansible Playbook 示例，用于在每个虚拟机实例上安装和配置 Kubernetes：

yaml
Copy code
---
- name: Install and Configure Kubernetes
  hosts: all
  become: true

  tasks:
    - name: Update apt cache
      apt:
        update_cache: yes

    - name: Install Docker
      apt:
        name: docker.io
        state: present

    - name: Add Kubernetes apt key
      apt_key:
        url: https://packages.cloud.google.com/apt/doc/apt-key.gpg
        state: present

    - name: Add Kubernetes repository
      apt_repository:
        repo: deb http://apt.kubernetes.io/ kubernetes-xenial main
        state: present

    - name: Install Kubernetes packages
      apt:
        name: kubelet kubeadm kubectl
        state: present

    - name: Enable and start kubelet service
      service:
        name: kubelet
        state: started
        enabled: yes

    - name: Disable swap
      shell: swapoff -a
      ignore_errors: yes

    - name: Set bridged IPv4 traffic to iptables chains
      sysctl:
        name: net.bridge.bridge-nf-call-iptables
        value: 1
        state: present
        sysctl_set: yes

    - name: Initialize Kubernetes cluster
      command: kubeadm init --pod-network-cidr=10.244.0.0/16

    - name: Copy kube config to user's home directory
      command: mkdir -p $HOME/.kube && cp /etc/kubernetes/admin.conf $HOME/.kube/config
      args:
        creates: $HOME/.kube/config

    - name: Install Flannel network plugin
      command: kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
      args:
        creates: /etc/kubernetes/manifests/kube-flannel.yml
请注意，上述 Playbook 中的任务包括以下操作：

更新 apt 缓存
安装 Docker 和 Kubernetes 相关软件包
启用和启动 kubelet 服务
禁用 swap 分区
配置网络设置
初始化 Kubernetes 集群
将 kubeconfig 文件复制到用户的主目录
安装 Flannel 网络插件
这只是一个简单的示例 Playbook，用于在每个虚拟机实例上进行基本的 Kubernetes 安装和配置。根据您的需求和环境，您可能需要添加更多的任务和配置。同时，您可以根据自己的网络需求选择适合的网络插件。

是的，您可以使用 GitHub Actions 来执行 Terraform 和 Ansible。下面是一个示例，演示如何在 GitHub Actions 中执行 Terraform 和 Ansible。

在您的 GitHub 仓库中创建一个名为 .github/workflows/main.yml 的文件。

在 main.yml 文件中添加以下代码：

yaml
Copy code
name: Terraform and Ansible Workflow

on:
  push:
    branches:
      - main

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repository
        uses: actions/checkout@v2

      - name: Setup Terraform
        uses: hashicorp/setup-terraform@v1
        with:
          terraform_version: <TERRAFORM_VERSION>

      - name: Initialize Terraform
        run: terraform init

      - name: Terraform Plan
        run: terraform plan

      - name: Terraform Apply
        run: terraform apply -auto-approve

      - name: Setup Ansible
        run: |
          sudo apt-get update
          sudo apt-get install -y ansible

      - name: Run Ansible Playbook
        run: ansible-playbook -i inventory playbook.yml
请确保将 <TERRAFORM_VERSION> 替换为您想要使用的 Terraform 版本。

在与 main.yml 文件相同的目录中，创建一个名为 inventory 的文件，并将您的主机和连接详细信息添加到其中。示例：
ini
Copy code
[webservers]
web1 ansible_host=<HOST_IP> ansible_user=<SSH_USERNAME> ansible_ssh_private_key_file=<SSH_PRIVATE_KEY_PATH>
将 <HOST_IP>、<SSH_USERNAME> 和 <SSH_PRIVATE_KEY_PATH> 替换为您的实际值。

在与 main.yml 文件相同的目录中，创建一个名为 playbook.yml 的 Ansible Playbook 文件，并在其中添加您的任务和配置。

提交并推送这些更改到您的 GitHub 仓库。

当您将更改推送到 GitHub 仓库的 main 分支时，GitHub Actions 将自动运行此工作流程。它将克隆仓库、设置 Terraform 和 Ansible 环境、执行 Terraform 初始化、计划和应用，并运行 Ansible Playbook 来配置远程主机。

请注意，上述代码仅提供了一个基本示例，用于演示如何在 GitHub Actions 中使用 Terraform 和 Ansible。根据您的需求和环境，您可能需要进行更多的自定义和配置。此外，确保您在 GitHub 仓库的 Secrets 中添加了所需的敏感信息（如 API 密钥、SSH 密钥等）以安全地访问和连接到远程资源。

更多关于 GitHub Actions 的配置和使用的详细信息，请参考 GitHub Actions 的官方文档。





User
我希望能手动控制运行，需要怎么做
ChatGPT
如果您希望手动控制 Terraform 和 Ansible 的运行，而不是在每次推送代码时自动触发执行，可以对 GitHub Actions 的工作流程进行一些修改。

打开 .github/workflows/main.yml 文件。

在 on 字段中删除或注释掉 push 触发器。示例：

yaml
Copy code
on:
  # push:
  #   branches:
  #     - main
在 jobs 下的 deploy 作业中添加一个新的 workflow_dispatch 触发器。示例：
yaml
Copy code
jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      # ... 其他步骤 ...

  manual-trigger:
    needs: deploy
    runs-on: ubuntu-latest
    steps:
      - name: Manual Trigger
        run: echo "This job is manually triggered"
上述示例中，我们添加了一个名为 manual-trigger 的作业，并将其设置为在 deploy 作业完成后运行。该作业仅包含一个简单的命令，以示例方式输出一条消息。

提交并推送这些更改到您的 GitHub 仓库。
现在，当您访问 GitHub 仓库的 Actions 页面时，您将看到一个名为 "Manual Trigger" 的工作流程，可以手动触发它。您可以点击 "Run workflow" 按钮来启动该工作流程，以便手动运行 Terraform 和 Ansible。

请注意，您可以根据需要对 manual-trigger 作业进行自定义。您可以添加其他步骤，如用户输入、确认步骤等，以便在手动触发时进行更多的交互和控制。

这样，您就可以手动控制 Terraform 和 Ansible 的执行，而不需要每次推送代码时都自动触发工作流程。
