Name:           license-tools
Version:        1.0
Release:        1%{?dist}
Summary:        license tools

License:        MIT
URL:            https://github.com/y2ghost
Source0:        %{name}-%{version}.tar.gz

BuildRequires:  gcc
BuildRequires:  make

%description
license tools

%prep
%autosetup

%build
make
make strip

%install
make install DESTDIR=%{buildroot}

%files
%license LICENSE
%dir /opt/license-tools/
/opt/license-tools/bin/generate-apply-file
/opt/license-tools/bin/print-license-file
/opt/license-tools/bin/import-license-file
/opt/license-tools/etc/public.key
/opt/license-tools/lib/license-tools.so
/opt/license-tools/output/

%post
chown root:root /opt/license-tools/bin/*
chmod u+s /opt/license-tools/bin/*

