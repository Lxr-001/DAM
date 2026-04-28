export const SearchQplMixin = {
  data() {
    return {}
  },
  methods: {
    // 在浏览器新建tab页，打开QPL管理界面，根据QPL号进行检索
    searchQplByQpl(record) {
      console.log('record: ', record)
      let qpl = record
      let newPage = this.$router.resolve({
        name: 'database',
        query: {
          activeKey: 'QPL管理',
          qpl: qpl
        }
      })
      // window.open(newPage.href, '_blank')
      setTimeout(() => {
        window.open(newPage.href)
      })
    }
  }
}
