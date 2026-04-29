<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="queryParams.userName" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="会员卡号" prop="cardNo">
              <el-input v-model="queryParams.cardNo" placeholder="请输入会员卡号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="会员等级" prop="cardLevel">
              <el-select v-model="queryParams.cardLevel" placeholder="请选择会员等级" clearable >
                <el-option v-for="dict in sp_member_level" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable >
                <el-option v-for="dict in sp_member_card_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:memberCard:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:memberCard:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:memberCard:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="memberCardList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="会员卡ID" align="center" prop="id" v-if="false" />
        <el-table-column label="用户名" align="center" prop="userName" width="120" />
        <el-table-column label="会员卡号" align="center" prop="cardNo" width="180" />
        <el-table-column label="会员等级" align="center" prop="cardLevel" width="120">
          <template #default="scope">
            <dict-tag :options="sp_member_level" :value="scope.row.cardLevel"/>
          </template>
        </el-table-column>
        <el-table-column label="余额" align="center" prop="balance" width="120" />
        <el-table-column label="累计充值金额" align="center" prop="totalRecharge" width="120" />
        <el-table-column label="累计消费金额" align="center" prop="totalConsume" width="120" />
        <el-table-column label="积分余额" align="center" prop="totalPoint" width="120" />
        <el-table-column label="免费次数" align="center" prop="freeTimes" width="100" />
        <el-table-column label="发卡日期" align="center" prop="issueDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.issueDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到期日期" align="center" prop="expiryDate" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.expiryDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="sp_member_card_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" width="150" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:memberCard:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:memberCard:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员卡对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="800px" append-to-body>
      <el-form ref="memberCardFormRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="form.userId" placeholder="请输入用户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员卡号" prop="cardNo">
              <el-input v-model="form.cardNo" placeholder="请输入会员卡号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会员等级" prop="cardLevel">
              <el-select v-model="form.cardLevel" placeholder="请选择会员等级" style="width: 100%">
                <el-option
                    v-for="dict in sp_member_level"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option
                    v-for="dict in sp_member_card_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="余额" prop="balance">
              <el-input-number v-model="form.balance" :min="0" :precision="2" placeholder="请输入余额" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="免费次数" prop="freeTimes">
              <el-input-number v-model="form.freeTimes" :min="0" :precision="0" placeholder="请输入免费次数" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="累计充值金额" prop="totalRecharge">
              <el-input-number v-model="form.totalRecharge" :min="0" :precision="2" placeholder="请输入累计充值金额" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="累计消费金额" prop="totalConsume">
              <el-input-number v-model="form.totalConsume" :min="0" :precision="2" placeholder="请输入累计消费金额" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="积分余额" prop="totalPoint">
              <el-input-number v-model="form.totalPoint" :min="0" :precision="0" placeholder="请输入积分余额" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发卡日期" prop="issueDate">
              <el-date-picker clearable
                v-model="form.issueDate"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择发卡日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="到期日期" prop="expiryDate">
              <el-date-picker clearable
                v-model="form.expiryDate"
                type="datetime"
                value-format="YYYY-MM-DD HH:mm:ss"
                placeholder="请选择到期日期"
                style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="MemberCard" lang="ts">
import { listMemberCard, getMemberCard, delMemberCard, addMemberCard, updateMemberCard } from '@/api/swimming/memberCard';
import { MemberCardVO, MemberCardQuery, MemberCardForm } from '@/api/swimming/memberCard/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_member_level, sp_member_card_status } = toRefs<any>(proxy?.useDict('sp_member_level', 'sp_member_card_status'));

const memberCardList = ref<MemberCardVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberCardFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberCardForm = {
  id: undefined,
  userId: undefined,
  cardNo: undefined,
  cardLevel: undefined,
  balance: undefined,
  totalRecharge: undefined,
  totalConsume: undefined,
  totalPoint: undefined,
  freeTimes: undefined,
  issueDate: undefined,
  expiryDate: undefined,
  status: undefined,
  remark: undefined,
}
const data = reactive<PageData<MemberCardForm, MemberCardQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userName: undefined,
    cardNo: undefined,
    cardLevel: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "会员卡ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    cardNo: [
      { required: true, message: "会员卡号不能为空", trigger: "blur" }
    ],
    issueDate: [
      { required: true, message: "发卡日期不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员卡列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberCard(queryParams.value);
  memberCardList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  memberCardFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: MemberCardVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员卡";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberCardVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberCard(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员卡";
}

/** 提交按钮 */
const submitForm = () => {
  memberCardFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberCard(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberCard(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberCardVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员卡编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberCard(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/memberCard/export', {
    ...queryParams.value
  }, `memberCard_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
