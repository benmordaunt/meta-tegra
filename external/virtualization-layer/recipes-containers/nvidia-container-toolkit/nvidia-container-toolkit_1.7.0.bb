SUMMARY = "NVIDIA container runtime hook"
DESCRIPTION = "NVIDIA container runtime hook \
Provides an OCI hook to enable GPU support in containers. \
"
#Home page for now
HOMEPAGE = "https://github.com/NVIDIA/nvidia-container-runtime"

LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
SRC_URI = "git://github.com/NVIDIA/container-toolkit;protocol=https"
SRCREV = "f10f533fb21ee4cd7c298681fc61cc5a1aa09551"

GO_IMPORT = "github.com/NVIDIA/${BPN}"
GO_INSTALL = "${GO_IMPORT}/${BPN}"

export GO111MODULE = "off"

S = "${WORKDIR}/git"

REQUIRED_DISTRO_FEATURES = "virtualization"

inherit go features_check

do_install_append(){
    install -d ${D}${sysconfdir}/nvidia-container-runtime ${D}${libexecdir}/oci/hooks.d ${D}/${datadir}/oci/hooks.d
    install ${S}/src/${GO_IMPORT}/config/config.toml.centos ${D}${sysconfdir}/nvidia-container-runtime/config.toml
    install ${S}/src/${GO_IMPORT}/oci-nvidia-hook ${D}${libexecdir}/oci/hooks.d
    install ${S}/src/${GO_IMPORT}/oci-nvidia-hook.json ${D}/${datadir}/oci/hooks.d
    ln -sf nvidia-container-toolkit ${D}${bindir}/nvidia-container-runtime-hook
}

FILES_${PN} += "${datadir}/oci"

RDEPENDS_${PN} = "\
    libnvidia-container-tools \
    docker-ce \
"
RDEPENDS_${PN}-dev += "bash make"