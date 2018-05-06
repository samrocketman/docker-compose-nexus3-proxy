# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  # The most common configuration options are documented and commented below.
  # For a complete reference, please see the online documentation at
  # https://docs.vagrantup.com.

  config.vm.box = "centos/7"

  config.vm.network "forwarded_port", guest: 8081, host: 8081, host_ip: "0.0.0.0"

  # Share an additional folder to the guest VM. The first argument is
  # the path on the host to the actual folder. The second argument is
  # the path on the guest to mount the folder. And the optional third
  # argument is a set of non-required options.
  # config.vm.synced_folder "../data", "/vagrant_data"

  config.vm.provider "virtualbox" do |vb|
    vb.memory = "4096"
  end

  config.vm.provision "shell", inline: <<-SHELL
    set -ex

    # install EPEL repo
    rpm -qa | grep -- epel-release || (
      yum makecache fast
      yum install -y epel-release
    )


    # install IUS repo
    rpm -qa | grep ius-release || (
      [ -r /tmp/ius.asc ] || curl -fLo /tmp/ius.asc https://dl.iuscommunity.org/pub/ius/IUS-COMMUNITY-GPG-KEY
      echo '688852e2dba88a3836392adfc5a69a1f46863b78bb6ba54774a50fdecee7e38e  /tmp/ius.asc' | sha256sum -c
      rpm --import /tmp/ius.asc
      [ -r /tmp/ius.rpm ] || curl -fLo /tmp/ius.rpm https://centos7.iuscommunity.org/ius-release.rpm
      rpm -K /tmp/ius.rpm
      yum localinstall -y /tmp/ius.rpm
    )


    # install Docker repo
    [ -f /etc/yum.repos.d/docker-ce.repo ] || yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo


    # install packages
    rpm -qa | grep -- docker-ce || (
      yum makecache
      yum install -y yum-utils device-mapper-persistent-data lvm2 git2u docker-ce
      yum install -y vim bind-utils net-tools nc ntpdate
      systemctl start ntpdate
      systemctl enable ntpdate
    )
    [ -f /etc/docker/daemon.json ] || (
      mkdir -p /etc/docker
      cat > /etc/docker/daemon.json <<EOF
{
    "hosts": [
        "unix:///var/run/docker.sock",
        "tcp://localhost:2375"
    ]
}
EOF
      chmod 700 /etc/docker
      chown root. /etc/docker/daemon.json
      chmod 644 /etc/docker/daemon.json
      systemctl enable docker
      systemctl start docker
    )
    if [ ! -f /etc/cron.d/nexus ]; then
      cat > /etc/cron.d/nexus <<EOF
@reboot /bin/bash -c 'cd /vagrant;docker-compose up -d'
EOF
    fi
    if [ ! -f /usr/local/sbin/docker-compose ]; then
      curl -Lo /usr/local/sbin/docker-compose https://github.com/docker/compose/releases/download/1.21.2/docker-compose-Linux-x86_64
      sha256sum -c - <<<'8a11713e11ed73abcb3feb88cd8b5674b3320ba33b22b2ba37915b4ecffdf042  /usr/local/sbin/docker-compose'
      chmod 755 /usr/local/sbin/docker-compose
    fi
    if ! nc -v localhost 8081; then
      ( cd /vagrant; docker-compose up -d )
    fi
  SHELL
end
