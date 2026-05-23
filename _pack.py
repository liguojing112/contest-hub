import zipfile
import os

root = r'c:\Workspace\JavaProjects\02-jingsai'
out = os.path.join(root, 'jingsai-v1.0.0.zip')

exclude_dirs = {'.git', '.claude', '.playwright-mcp', '__pycache__', 'node_modules', 'logs', 'data', '.vscode'}
exclude_ext = {'.log', '.db', '.pyc', '.png', '.zip', '.gz', '.7z', '.rar', '.original'}
exclude_files = {
    'api-design.md', 'API_INTEGRATION_GUIDE.md', 'api_test.html', 'logs.html',
    'admin_competitions.html', 'admin_students.html', 'admin_teachers.html',
    'teacher_active.html', 'teacher_competitions.html', 'teacher_registrations.html',
    'pack.py', '_pack.py',
    '启动系统.bat', '直接启动.bat',
}

with zipfile.ZipFile(out, 'w', zipfile.ZIP_DEFLATED) as zf:
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in exclude_dirs]

        for fname in filenames:
            if fname in exclude_files:
                continue
            ext = os.path.splitext(fname)[1].lower()
            if ext in exclude_ext:
                continue

            full_path = os.path.join(dirpath, fname)
            rel_path = os.path.relpath(full_path, root).replace('\\', '/')
            parts = rel_path.split('/')

            # In target/ only keep jingsai-1.0.0.jar itself (no classes, no other jars)
            if 'target' in parts:
                if fname != 'jingsai-1.0.0.jar':
                    continue

            zf.write(full_path, rel_path)

# Verify
with zipfile.ZipFile(out, 'r') as zf:
    names = zf.namelist()
    checks = ['jingsai-1.0.0.jar', 'start.bat', 'application.yml', 'index.html',
              'admin.html', 'student.html', 'teacher.html', 'schema.sql', 'api.js']
    for c in checks:
        found = [n for n in names if n.endswith(c)]
        status = "OK" if found else "MISSING!"
        info = found[0] if found else "N/A"
        print(f'  {c}: {status} -> {info}')

    jar = [n for n in names if n.endswith('.jar')]
    print(f'\nTotal files: {len(names)}')
    print(f'JAR files in package: {jar}')
    print(f'Top-level items:')
    top = set(n.split('/')[0] for n in names)
    for t in sorted(top):
        print(f'  {t}/')

size_mb = os.path.getsize(out) / 1024 / 1024
print(f'\nSize: {size_mb:.1f} MB')
print(f'Output: {out}')
