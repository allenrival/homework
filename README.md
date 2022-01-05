# homework

测试项目只有TestRepeatedFailures和TestRepeatedFailures没有通过，其他都已经通过

测试结果

```json
{"tests": [
  {
    "output": "TEST SUCCEEDED.\nRecovery used 124 iops on the log.\nMaximum log size reached during workload: 8760\n",
    "score": 5,
    "number": "4",
    "visibility": "visible",
    "max_score": 5,
    "name": "TestRecoveryPerformance2"
  },
  {
    "output": "TEST SUCCEEDED.\n",
    "score": 3,
    "number": "2",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestRecovery2"
  },
  {
    "output": "TEST FAILED.\nAfter 1 crashes, value for key 3 did not match what was committed (wanted [-2, 7, -8..., got [14, -45, ...).",
    "score": 0,
    "number": "4",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestRepeatedFailures2"
  },
  {
    "output": "TEST SUCCEEDED.\nRecovery used 378 iops on the log.\nMaximum log size reached during workload: 6426\n",
    "score": 5,
    "number": "4",
    "visibility": "visible",
    "max_score": 5,
    "name": "TestRecoveryPerformance"
  },
  {
    "output": "TEST SUCCEEDED.\nnull\r\n[1, 2, 3, 4, 5, 6, 7, 8]\r\n[1, 2, 3, 4, 5, 6, 7, 8]\r\n",
    "score": 3,
    "number": "2",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestRecovery"
  },
  {
    "output": "TEST SUCCEEDED.\nnull\r\n[1, 2, 3, 4, 5, 6, 7, 8]\r\n",
    "score": 0,
    "number": "1",
    "visibility": "visible",
    "max_score": 0,
    "name": "TestTransaction"
  },
  {
    "output": "TEST SUCCEEDED.\n",
    "score": 3,
    "number": "2",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestBigWriteRecovery"
  },
  {
    "output": "TEST SUCCEEDED.\n",
    "score": 0,
    "number": "1",
    "visibility": "visible",
    "max_score": 0,
    "name": "TestBigWrite"
  },
  {
    "output": "TEST FAILED.\nAfter 1 crashes, value for key 1 did not match what was committed (wanted [61, -29, ..., got [-93, -58,...).",
    "score": 0,
    "number": "4",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestRepeatedFailures"
  },
  {
    "output": "TEST SUCCEEDED.\n",
    "score": 0,
    "number": "1",
    "visibility": "visible",
    "max_score": 0,
    "name": "TestTransaction2"
  },
  {
    "output": "TEST SUCCEEDED.\nT2's last TXN is committed.\r\nT1's last TXN is committed.\r\nT1's last TXN is committed.\r\n",
    "score": 0,
    "number": "2",
    "visibility": "visible",
    "max_score": 0,
    "name": "TestCoupledWrites"
  },
  {
    "output": "TEST SUCCEEDED.\nT1's last TXN is committed.\r\nT1's last TXN is committed.\r\nT2's last TXN is committed.\r\n",
    "score": 3,
    "number": "2",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestBigTransactions"
  },
  {
    "output": "TEST SUCCEEDED.\nT1's last TXN is committed.\r\nT1's last committed TXN is queued to the storage manager.\r\nT1's last committed TXN is visible after recovery.\r\nT1's last committed TXN is queued to the storage manager after recovery.\r\nT1's last TXN is committed.\r\nT1's last committed TXN is queued to the storage manager.\r\nT1's last committed TXN is visible after recovery.\r\nT1's last committed TXN is queued to the storage manager after recovery.\r\nT1's last TXN is committed.\r\nT1's last committed TXN is queued to the storage manager.\r\nT1's last committed TXN is visible after recovery.\r\nT1's last committed TXN is queued to the storage manager after recovery.\r\n",
    "score": 3,
    "number": "2",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestBigTransactionsRecovery"
  },
  {
    "output": "TEST SUCCEEDED.\nT1's last TXN is committed.\r\nT1's last committed TXN is queued to the storage manager.\r\nT1's last committed TXN is visible after recovery.\r\nT1's last committed TXN is queued to the storage manager after recovery.\r\nT1's last TXN is committed.\r\nT1's last committed TXN is queued to the storage manager.\r\nT1's last committed TXN is visible after recovery.\r\nT1's last committed TXN is queued to the storage manager after recovery.\r\nT1's last TXN is committed.\r\nT1's last committed TXN is queued to the storage manager.\r\nT1's last committed TXN is visible after recovery.\r\nT1's last committed TXN is queued to the storage manager after recovery.\r\n",
    "score": 3,
    "number": "2",
    "visibility": "visible",
    "max_score": 3,
    "name": "TestCoupledWritesRecovery"
  }
]}
```

# homework
