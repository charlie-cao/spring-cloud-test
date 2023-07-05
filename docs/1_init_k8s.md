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
