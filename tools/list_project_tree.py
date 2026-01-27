import os
import sys

"""
Project File Finder (The "Map Maker")

Purpose:
This script scans your project directory and creates a clean list of all 
relevant files. This list is used by the 'ReConstructor' to build the 
AI's context.

How it works:
1. It walks through every folder in your project.
2. It ignores 'junk' folders (like .git, __pycache__, node_modules).
3. It ignores binary files (images, zips) that the AI can't read.
4. It prints the clean paths to the console (standard output).

Usage:
python tools/list_project_tree.py > reconstructor-file-list.txt
"""

def list_project_files(startpath):
    """
    Scans the project directory and prints a clean list of file paths.
    Intended to be piped into 'reconstructor-file-list.txt'.
    """

    ignore_dirs = {
        '.git', '.venv', '__pycache__', '.vscode', 'node_modules', 
        'build', 'dist', 'tools', 'X_Intels', 'X_Repos'
    }
    
    ignore_files = {
        '.DS_Store', 'Thumbs.db', 'requirements.txt',
        'SERIALIZED_CONTEXT.txt', 'RECONSTRUCTOR_CONTEXT.md', 'START_CONTEXT.md',
        'reconstructor-file-list.txt', '_SESSION_ADDENDUM_BUFFER.md',
        'package-lock.json', 'yarn.lock', 'pnpm-lock.yaml', 'yarn-error.log', 
        'pnpm-error.log', 'yarn-error.log', '.gitignore', '.gitkeep'
    }
    
    ignore_extensions = {
        '.png', '.jpg', '.jpeg', '.gif', '.ico', 
        '.pdf', '.zip', '.7z', '.exe', '.dll', 
        '.pyc', '.blend', '.fbx', '.obj'
    }

    for root, dirs, files in os.walk(startpath, topdown=True):
        dirs[:] = [d for d in dirs if d not in ignore_dirs]

        for name in files:
            if name in ignore_files:
                continue
            
            _, ext = os.path.splitext(name)
            if ext.lower() in ignore_extensions:
                continue
                
            full_path = os.path.join(root, name)
            
            relative_path = os.path.relpath(full_path, startpath)
            
            clean_path = relative_path.replace(os.sep, '/')
            
            print(clean_path)

if __name__ == "__main__":
    project_root = "."
    
    if len(sys.argv) > 1:
        project_root = sys.argv[1]
        
    list_project_files(project_root)
    