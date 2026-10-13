import os
import zipfile

def create_project_zip():
    zip_path = 'public/basma-alarm-project.zip'
    os.makedirs('public', exist_ok=True)
    
    # Exclude unnecessary heavy or temporary files
    exclude_dirs = {
        'node_modules', '.git', 'dist', '.aistudio', '__pycache__', '.next', 'build'
    }
    exclude_files = {
        '.DS_Store', 'basma-alarm-project.zip', 'package-lock.json'
    }

    print("Building ZIP archive...")
    with zipfile.ZipFile(zip_path, 'w', zipfile.ZIP_DEFLATED) as zf:
        for root, dirs, files in os.walk('.'):
            # Modify dirs in-place to avoid descending into excluded folders
            dirs[:] = [d for d in dirs if d not in exclude_dirs and not d.startswith('.')]
            
            for f in files:
                if f in exclude_files or f.endswith('.zip'):
                    continue
                file_path = os.path.join(root, f)
                # Relative path inside the archive
                arcname = os.path.relpath(file_path, '.')
                # Prefix with project folder name for clean unzipping
                arcname = os.path.join('basma-alarm-app', arcname)
                zf.write(file_path, arcname)

    size_mb = os.path.getsize(zip_path) / (1024 * 1024)
    print(f"Archive created at {zip_path} ({size_mb:.2f} MB)")

if __name__ == '__main__':
    create_project_zip()
