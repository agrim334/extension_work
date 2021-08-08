git add .
git rm --cached Python_compare/all_projects/ -r
git rm --cached Python_compare/selected/XSStrike/ -r
git rm --cached Python_compare/selected/PythonPlantsVsZombies/ -r
git rm --cached Python_compare/selected/rebound/ -r
read com_var
git commit -m "$com_var"
git push git@github.com:agrim334/extension_work.git