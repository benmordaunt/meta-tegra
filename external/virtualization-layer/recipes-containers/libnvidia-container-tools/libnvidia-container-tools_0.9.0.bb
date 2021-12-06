SUMMARY = "NVIDIA container runtime library"

DESCRIPTION = "NVIDIA container runtime library \
The nvidia-container library provides an interface to configure GNU/Linux \
containers leveraging NVIDIA hardware. The implementation relies on several \
kernel subsystems and is designed to be agnostic of the container runtime. \
"
HOMEPAGE = "https://github.com/NVIDIA/libnvidia-container"

DEPENDS = " \
    coreutils-native \
    pkgconfig-native \
    libcap \
    elfutils \
    libtirpc \
    ldconfig-native \
    ca-certificates-native \
    curl-native \
"
LICENSE = "BSD-3-Clause & GPLv3 & Proprietary"

CURL_CA_BUNDLE = "${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt"
export CURL_CA_BUNDLE

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
    file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
    file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
"

PR = "r1"

NVIDIA_MODPROBE_VERSION = "396.51"
ELF_TOOLCHAIN_VERSION = "0.7.1"
LIBTIRPC_VERSION = "1.1.4"

SRC_URI = "git://github.com/NVIDIA/libnvidia-container.git;protocol=https;name=libnvidia;branch=master \
           git://github.com/NVIDIA/nvidia-modprobe.git;protocol=https;branch=main;name=modprobe;destsuffix=git/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION} \
           file://0001-Makefile-Fix-RCP-flags-and-change-path-definitions-s.patch \
           file://0004-Fix-build.h-generation-for-cross-builds.patch \
"

SRC_URI[modprobe.md5sum] = "f82b649e7a0f1d1279264f9494e7cf43"
SRC_URI[modprobe.sha256sum] = "25bc6437a384be670e9fd76ac2e5b9753517e23eb16e7fa891b18537b70c4b20"


SRCREV_libnvidia = "f37bb387ad05f6e501069d99e4135a97289faf1f"
# Nvidia modprobe version 396.51
SRCREV_modprobe = "d97c08af5061f1516fb2e3a26508936f69d6d71d"

S = "${WORKDIR}/git"

PACKAGECONFIG ??= ""
PACKAGECONFIG[seccomp] = "WITH_SECCOMP=yes,WITH_SECCOMP=no,libseccomp"

def build_date(d):
    import datetime
    epoch = d.getVar('SOURCE_DATE_EPOCH')
    if epoch:
        dt = datetime.datetime.fromtimestamp(int(epoch), tz=datetime.timezone.utc)
        return 'DATE=' + dt.isoformat(timespec='minutes')
    return ''

# We need to link with libelf, otherwise we need to
# include bmake-native which does not exist at the moment.
EXTRA_OEMAKE = "EXCLUDE_BUILD_FLAGS=1 PLATFORM=${HOST_ARCH} WITH_LIBELF=yes ${@build_date(d)} ${PACKAGECONFIG_CONFARGS}"

CFLAGS_prepend = " -I=/usr/include/tirpc "

export OBJCPY="${OBJCOPY}"

# Fix me: Create an independent recipe for nvidia-modprobe
do_configure_append() {
    # Mark Nvidia modprobe as downloaded
    touch ${S}/deps/src/nvidia-modprobe-${NVIDIA_MODPROBE_VERSION}/.download_stamp
}

do_install () {
    oe_runmake install DESTDIR=${D}
}

INSANE_SKIP_${PN} = "already-stripped"
