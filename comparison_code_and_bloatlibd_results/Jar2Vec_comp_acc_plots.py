import matplotlib.pyplot as plt
import numpy as np

# Fixing random state for reproducibility
np.random.seed(19680801)

plt.rcdefaults()
fig, ax = plt.subplots()

tools = ('LiteRadar', 'LibScout', 'LibD', 'Lib2Vec')
y_pos = np.arange(len(tools))
acc_values = [10, 25, 100, 100]
error = np.random.rand(len(tools))

ax.barh(y_pos, acc_values, xerr=error, align='center')
ax.set_yticks(y_pos)
ax.set_yticklabels(tools)

ax.invert_yaxis()  # labels read top-to-bottom
ax.set_xlabel('Accuracy')
ax.set_ylabel('TPL detection tools')
plt.tight_layout()
plt.show()
